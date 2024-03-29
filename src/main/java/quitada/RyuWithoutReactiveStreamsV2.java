package quitada;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

public class RyuWithoutReactiveStreamsV2 extends Application {
  public static void main(String[] args) {
    launch(args);
  }

  static Scene mainScene;
  static GraphicsContext graphicsContext;

  static final int WIDTH = 924;
  static final int HEIGHT = 784;
  static final int MARGIN = 32;
  static final int KEY_WIDTH = 245;
  static final int KEY_HIGHT = 239;
  static final int RYU_POS_X = MARGIN *2 + KEY_WIDTH *2;
  static final int RYU_POS_Y = KEY_HIGHT *2;
  static final int DEFAULT_FRAME = 10;

  static final String UP = "W";
  static final String LEFT = "A";
  static final String RIGHT = "S";
  static final String DOWN = "Z";
  static final String PUNCH = "X";
  static final String KICK = "C";
  static final String[] SYORYU_CL = {"SsZzSZX","SsZzZSX"};
  static final String[] HADOU_CL = {"ZzZSzsSX","ZzZSszSX","ZzSZzsSX","ZzSZszSX"};
  static final String[] TATSUMAKI_CL = {"ZzZAzaAC","ZzZAazAC","ZzAZazAC","ZzAZzaAC"};

  static Image up;
  static Image upGreen;

  static Image down;
  static Image downGreen;

  static Image left;
  static Image leftGreen;

  static Image right;
  static Image rightGreen;

  static Image punch;
  static Image punchGreen;

  static Image kick;
  static Image kickGreen;

  static Image ryuKamae[];
  static Image ryuSyoryuken[];
  static Image ryuTatsumaki[];
  static Image ryuPunch[];
  static Image ryuKick[];
  static Image ryuHadou[];


  static HashSet<String> currentlyActiveKeys;
  static List<String> commandList;

  static long stime = 0;
  static long foreno = -1;
  static long animationFrame = DEFAULT_FRAME;
  static int ryuAction = 0;

  static Media media;
  static MediaPlayer mp;

  @Override
  public void start(Stage mainStage) {
    mainStage.setTitle("Streamless Fighter II - Ryu's Deathblows tester");

    Group root = new Group();
    mainScene = new Scene(root);
    mainStage.setScene(mainScene);

    Canvas canvas = new Canvas(WIDTH, HEIGHT);
    root.getChildren().add(canvas);

    prepareActionHandlers();

    graphicsContext = canvas.getGraphicsContext2D();

    /*String[] formatNames= ImageIO.getWriterFormatNames();
    for(int i=0;i < formatNames.length ; i++){
      System.out.print(formatNames[i] + " ");
    }
    System.out.print("\n");*/

    loadGraphics();

    commandList = new ArrayList<String>();

    new AnimationTimer() {
      long interval = 40 * 1000000L;   //40 millisecs
      public void handle(long currentNanoTime) {
        // real time key animation handling
        keyAnimation();

        // Ryu animation handling - not so real time (update every "interval" nanoseconds)
        if( stime == 0 ) {
          stime = currentNanoTime;
        }
        Long no = (Long)(( currentNanoTime - stime ) / interval );
        if ( foreno != no ) {
          foreno = no;
          ryuAnimation((int)(foreno % animationFrame));
        }
      }
    }.start();

    //String path = getResource("ryu-stage.mp3").toString();
    /*media = new Media(getResource("ryu-stage.mp3").toString());
    mp = new MediaPlayer(media);
    mp.setCycleCount(MediaPlayer.INDEFINITE);
    mp.setVolume(0.5);
    mp.play();*/

    mainStage.show();
  }

  private static void prepareActionHandlers() {
    // use a set so duplicates are not possible
    currentlyActiveKeys = new HashSet<String>();
    mainScene.setOnKeyPressed(new EventHandler<KeyEvent>() {
      @Override
      public void handle(KeyEvent event) {
        String keyEvent = event.getCode().toString();
        System.out.println(keyEvent + " is pressed.");
        currentlyActiveKeys.add(keyEvent);
        if (keyActionChecker(keyEvent)) {
          commandList.add(keyEvent);
          if (keyEvent.equals(PUNCH) || keyEvent.equals(KICK)) {
            String cl = readCommand();
            /*try {
              Thread currentThread = Thread.currentThread();
              currentThread.sleep(13000);
            } catch (InterruptedException iex) {
              ;
            }*/
            if (cl.contains(SYORYU_CL[0]) || cl.contains(SYORYU_CL[1])) {
              startAction(3, 17, "shouryuuken.mp3");
            } else if (cl.contains(HADOU_CL[0]) || cl.contains(HADOU_CL[1]) || cl.contains(HADOU_CL[2]) || cl.contains(HADOU_CL[3])) {
              startAction(4, 14, "hadouken.mp3");
            } else if (cl.contains(TATSUMAKI_CL[0]) || cl.contains(TATSUMAKI_CL[1]) || cl.contains(TATSUMAKI_CL[2]) || cl.contains(TATSUMAKI_CL[3])) {
              startAction(5, 27, "tatsumaki_senpukyaku.mp3");
            } else if (cl.contains(PUNCH)) {
              startAction(1, 8, "punch.mp3");
            } else {
              // should be KICK
              startAction(2, 15, "kick.mp3");
            }
          }
        }
      }
    });
    mainScene.setOnKeyReleased(new EventHandler<KeyEvent>() {
      @Override
      public void handle(KeyEvent event) {
        String keyEvent = event.getCode().toString();
        System.out.println(keyEvent + " is released.");
        currentlyActiveKeys.remove(keyEvent);
        if (keyActionChecker(keyEvent)) {
          commandList.add(keyEvent.toLowerCase());
        }
      }
    });
  }

  private static void loadGraphics() {
    left = new Image(getResource("left.png"));
    leftGreen = new Image(getResource("leftG.png"));

    right = new Image(getResource("right.png"));
    rightGreen = new Image(getResource("rightG.png"));

    up = new Image(getResource("up.png"));
    upGreen = new Image(getResource("upG.png"));

    down = new Image(getResource("down.png"));
    downGreen = new Image(getResource("downG.png"));

    punch = new Image(getResource("punch.png"));
    punchGreen = new Image(getResource("punchG.png"));

    kick = new Image(getResource("kick.png"));
    kickGreen = new Image(getResource("kickG.png"));

    // ryu space 336 x 414 -> scale factor x 2.07

    // kamae[10] 78 x 111 -> 161 x 230
    ryuKamae = new Image[]{
        new Image(getResource("ryu-kamae-0.gif")),
        new Image(getResource("ryu-kamae-1.gif")),
        new Image(getResource("ryu-kamae-2.gif")),
        new Image(getResource("ryu-kamae-3.gif")),
        new Image(getResource("ryu-kamae-4.gif")),
        new Image(getResource("ryu-kamae-5.gif")),
        new Image(getResource("ryu-kamae-6.gif")),
        new Image(getResource("ryu-kamae-7.gif")),
        new Image(getResource("ryu-kamae-8.gif")),
        new Image(getResource("ryu-kamae-9.gif"))
    };

    // punch[8] 127 x 105 -> 263 x 217
    ryuPunch = new Image[]{
        new Image(getResource("ryu-punch-0.gif")),
        new Image(getResource("ryu-punch-1.gif")),
        new Image(getResource("ryu-punch-2.gif")),
        new Image(getResource("ryu-punch-3.gif")),
        new Image(getResource("ryu-punch-4.gif")),
        new Image(getResource("ryu-punch-5.gif")),
        new Image(getResource("ryu-punch-6.gif")),
        new Image(getResource("ryu-punch-7.gif")),
    };

    // kick[15] 148 x 116 -> 306 x 240
    ryuKick = new Image[]{
        new Image(getResource("ryu-kick-0.gif")),
        new Image(getResource("ryu-kick-1.gif")),
        new Image(getResource("ryu-kick-2.gif")),
        new Image(getResource("ryu-kick-3.gif")),
        new Image(getResource("ryu-kick-4.gif")),
        new Image(getResource("ryu-kick-5.gif")),
        new Image(getResource("ryu-kick-6.gif")),
        new Image(getResource("ryu-kick-7.gif")),
        new Image(getResource("ryu-kick-8.gif")),
        new Image(getResource("ryu-kick-9.gif")),
        new Image(getResource("ryu-kick-10.gif")),
        new Image(getResource("ryu-kick-11.gif")),
        new Image(getResource("ryu-kick-12.gif")),
        new Image(getResource("ryu-kick-13.gif")),
        new Image(getResource("ryu-kick-14.gif"))
    };

    // hadoken[14] 136 x 104 -> 282 x 215
    ryuHadou = new Image[]{
        new Image(getResource("ryu-hadou-0.gif")),
        new Image(getResource("ryu-hadou-1.gif")),
        new Image(getResource("ryu-hadou-2.gif")),
        new Image(getResource("ryu-hadou-3.gif")),
        new Image(getResource("ryu-hadou-4.gif")),
        new Image(getResource("ryu-hadou-5.gif")),
        new Image(getResource("ryu-hadou-6.gif")),
        new Image(getResource("ryu-hadou-7.gif")),
        new Image(getResource("ryu-hadou-8.gif")),
        new Image(getResource("ryu-hadou-9.gif")),
        new Image(getResource("ryu-hadou-10.gif")),
        new Image(getResource("ryu-hadou-11.gif")),
        new Image(getResource("ryu-hadou-12.gif")),
        new Image(getResource("ryu-hadou-13.gif"))
    };

    // shoryuken[17] 102 x 200 -> 211 x 414
    ryuSyoryuken = new Image[]{
        new Image(getResource("ryu-syoryuken-0.gif")),
        new Image(getResource("ryu-syoryuken-1.gif")),
        new Image(getResource("ryu-syoryuken-2.gif")),
        new Image(getResource("ryu-syoryuken-3.gif")),
        new Image(getResource("ryu-syoryuken-4.gif")),
        new Image(getResource("ryu-syoryuken-5.gif")),
        new Image(getResource("ryu-syoryuken-6.gif")),
        new Image(getResource("ryu-syoryuken-7.gif")),
        new Image(getResource("ryu-syoryuken-8.gif")),
        new Image(getResource("ryu-syoryuken-9.gif")),
        new Image(getResource("ryu-syoryuken-10.gif")),
        new Image(getResource("ryu-syoryuken-11.gif")),
        new Image(getResource("ryu-syoryuken-12.gif")),
        new Image(getResource("ryu-syoryuken-13.gif")),
        new Image(getResource("ryu-syoryuken-14.gif")),
        new Image(getResource("ryu-syoryuken-15.gif")),
        new Image(getResource("ryu-syoryuken-16.gif"))
    };

    // tatsumaki senpukyaku[27] 159 x 140 -> 329 x 290
    ryuTatsumaki = new Image[]{
        new Image(getResource("ryu-tasumaki-0.gif")),
        new Image(getResource("ryu-tasumaki-1.gif")),
        new Image(getResource("ryu-tasumaki-2.gif")),
        new Image(getResource("ryu-tasumaki-3.gif")),
        new Image(getResource("ryu-tasumaki-4.gif")),
        new Image(getResource("ryu-tasumaki-5.gif")),
        new Image(getResource("ryu-tasumaki-6.gif")),
        new Image(getResource("ryu-tasumaki-7.gif")),
        new Image(getResource("ryu-tasumaki-8.gif")),
        new Image(getResource("ryu-tasumaki-9.gif")),
        new Image(getResource("ryu-tasumaki-10.gif")),
        new Image(getResource("ryu-tasumaki-11.gif")),
        new Image(getResource("ryu-tasumaki-12.gif")),
        new Image(getResource("ryu-tasumaki-13.gif")),
        new Image(getResource("ryu-tasumaki-14.gif")),
        new Image(getResource("ryu-tasumaki-15.gif")),
        new Image(getResource("ryu-tasumaki-16.gif")),
        new Image(getResource("ryu-tasumaki-17.gif")),
        new Image(getResource("ryu-tasumaki-18.gif")),
        new Image(getResource("ryu-tasumaki-19.gif")),
        new Image(getResource("ryu-tasumaki-20.gif")),
        new Image(getResource("ryu-tasumaki-21.gif")),
        new Image(getResource("ryu-tasumaki-22.gif")),
        new Image(getResource("ryu-tasumaki-23.gif")),
        new Image(getResource("ryu-tasumaki-24.gif")),
        new Image(getResource("ryu-tasumaki-25.gif")),
        new Image(getResource("ryu-tasumaki-26.gif"))
    };
  }

  private static String getResource(String filename) {
    return RyuWithoutReactiveStreamsV2.class.getResource(filename).toString();
  }

  private static void keyAnimation() {
    if (currentlyActiveKeys.contains(UP)) {
      graphicsContext.drawImage(upGreen, MARGIN + (KEY_WIDTH /2) , MARGIN);
    } else {
      graphicsContext.drawImage(up, MARGIN + (KEY_WIDTH /2) , MARGIN);
    }

    if (currentlyActiveKeys.contains(DOWN)) {
      graphicsContext.drawImage(downGreen, MARGIN + (KEY_WIDTH /2) ,MARGIN + KEY_HIGHT *2);
    } else {
      graphicsContext.drawImage(down, MARGIN + (KEY_WIDTH /2) ,MARGIN + KEY_HIGHT *2);
    }

    if (currentlyActiveKeys.contains(LEFT)) {
      graphicsContext.drawImage(leftGreen, MARGIN ,MARGIN + KEY_HIGHT);
    } else {
      graphicsContext.drawImage(left, MARGIN ,MARGIN + KEY_HIGHT);
    }

    if (currentlyActiveKeys.contains(RIGHT)) {
      graphicsContext.drawImage(rightGreen, MARGIN + KEY_WIDTH, MARGIN + KEY_HIGHT);
    } else {
      graphicsContext.drawImage(right, MARGIN + KEY_WIDTH, MARGIN + KEY_HIGHT);
    }

    if (currentlyActiveKeys.contains(PUNCH)) {
      graphicsContext.drawImage(punchGreen, MARGIN + (KEY_WIDTH * 1.5), MARGIN + KEY_HIGHT *2);
    } else {
      graphicsContext.drawImage(punch, MARGIN + (KEY_WIDTH * 1.5), MARGIN + KEY_HIGHT *2);
    }

    if (currentlyActiveKeys.contains(KICK)) {
      graphicsContext.drawImage(kickGreen, MARGIN + (KEY_WIDTH * 2.5), MARGIN + KEY_HIGHT *2);
    } else {
      graphicsContext.drawImage(kick, MARGIN + (KEY_WIDTH * 2.5), MARGIN + KEY_HIGHT *2);
    }
  }

  private static void ryuAnimation(int mod) {
    graphicsContext.clearRect(RYU_POS_X , MARGIN, KEY_WIDTH *1.5 - MARGIN, RYU_POS_Y - MARGIN);
    switch (ryuAction) {
      case 1:
        graphicsContext.drawImage(ryuPunch[mod], RYU_POS_X, RYU_POS_Y - 217, 263, 217);
        if (mod == 7) {
          reset();
        }
        break;
      case 2:
        graphicsContext.drawImage(ryuKick[mod], RYU_POS_X , RYU_POS_Y - 240, 306, 240);
        if (mod == 14) {
          reset();
        }
        break;
      case 3:
        graphicsContext.drawImage(ryuSyoryuken[mod], RYU_POS_X , RYU_POS_Y - 414, 211, 414);
        if (mod == 16) {
          reset();
        }
        break;
      case 4:
        graphicsContext.drawImage(ryuHadou[mod], RYU_POS_X , RYU_POS_Y - 215, 282, 215);
        if (mod == 13) {
          reset();
        }
        break;
      case 5:
        graphicsContext.drawImage(ryuTatsumaki[mod], RYU_POS_X , RYU_POS_Y - 290, 329, 290);
        if (mod == 26) {
          reset();
        }
        break;
      default:
        graphicsContext.drawImage(ryuKamae[mod], RYU_POS_X , RYU_POS_Y - 230, 161, 230);
    }
  }

  private static String readCommand() {
    String cl = "";
    for (String str : commandList) {
      cl = cl + str;
    }
    //System.out.println("commandlist=" + cl);
    return cl;
  }

  private static void ryuVoice(String file) {
    AudioClip clip = new AudioClip(getResource(file).toString());
    // need to set rate other than default value (i.e, 1) to prevent audio issue, somehow
    clip.setRate(0.99);
    clip.play();
  }

  private static void reset() {
    ryuAction = 0;
    animationFrame = DEFAULT_FRAME;
  }

  private static void startAction(int action, long frame, String voice) {
    stime = 0;
    foreno = -1;
    ryuVoice(voice);
    ryuAction = action;
    animationFrame = frame;
    commandList.clear();
  }

  private static boolean keyActionChecker(String keyEvent) {
    return keyEvent.equals(UP) || keyEvent.equals(LEFT) || keyEvent.equals(RIGHT) || keyEvent.equals(DOWN) || keyEvent.equals(PUNCH) || keyEvent.equals(KICK);
  }
}