package quitada;

import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.HashSet;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.scheduler.Schedulers;

public class RyuWithReactiveStreamsV3 extends Application {
  static private Scene mainScene;
  static private GraphicsContext graphicsContext;

  static private final int WIDTH = 924;
  static private final int HEIGHT = 784;
  static private final int MARGIN = 32;
  static private final int KEY_WIDTH = 245;
  static private final int KEY_HIGHT = 239;
  static private final int RYU_POS_X = MARGIN *2 + KEY_WIDTH *2;
  static private final int RYU_POS_Y = KEY_HIGHT *2;
  static private final int DEFAULT_FRAME = 10;
  static private final int STATIC_MARGIN = 35;
  static private final int TATSUMAKI_MARGIN = 35;

  static private final String UP = "W";
  static private final String LEFT = "A";
  static private final String RIGHT = "S";
  static private final String DOWN = "Z";
  static private final String PUNCH = "X";
  static private final String KICK = "C";
  static private final String[] SYORYU_CL = {"SsZzSZX","SsZzZSX"};
  static private final String[] HADOU_CL = {"ZzZSzsSX","ZzZSszSX","ZzSZzsSX","ZzSZszSX"};
  static private final String[] TATSUMAKI_CL = {"ZzZAzaAC","ZzZAazAC","ZzAZazAC","ZzAZzaAC"};

  static private Image up;
  static private Image upGreen;

  static private Image down;
  static private Image downGreen;

  static private Image left;
  static private Image leftGreen;

  static private Image right;
  static private Image rightGreen;

  static private Image punch;
  static private Image punchGreen;

  static private Image kick;
  static private Image kickGreen;

  static private Image ryuKamae[];
  static private Image ryuWalkF[];
  static private Image ryuWalkB[];
  static private Image ryuSyoryuken[];
  static private Image ryuTatsumaki[];
  static private Image ryuPunch[];
  static private Image ryuKick[];
  static private Image ryuHadou[];

  static private HashSet<String> currentlyActiveKeys;

  static private long stime = 0;
  static private long foreno = -1;
  static private long animationFrame = DEFAULT_FRAME;
  static private int ryuAction = 0;

  static private Media media;
  static private MediaPlayer mp;

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage mainStage) {
    mainStage.setTitle("Stream Fighter III - Ryu's Deathblows tester based on Reactive Streams");

    Group root = new Group();
    mainScene = new Scene(root);
    mainStage.setScene(mainScene);

    Canvas canvas = new Canvas(WIDTH + TATSUMAKI_MARGIN, HEIGHT);
    root.getChildren().add(canvas);

    prepareActionHandlers();

    graphicsContext = canvas.getGraphicsContext2D();

    loadGraphics();

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

    media = new Media(getResource("ryu-stage.mp3"));
    mp = new MediaPlayer(media);
    mp.setCycleCount(MediaPlayer.INDEFINITE);
    mp.setVolume(0.5);
    mp.play();

    mainStage.show();
  }

  private static void prepareActionHandlers() {
    // use a set so duplicates are not possible
    currentlyActiveKeys = new HashSet<String>();

    //Schedulers.DEFAULT_POOL_SIZE = 8;

    Flux flux = Flux.create(sink -> {
      mainScene.setOnKeyPressed(keyEvent -> {
        String pressedKey = keyEvent.getCode().toString();
        currentlyActiveKeys.add(pressedKey);
        if (pressedKey.equals(RIGHT) && ryuAction != 6) {
          resetAction(6,11, null);
        } else if (pressedKey.equals(LEFT) && ryuAction != 7) {
          resetAction(7,11,null);
        }
        sink.next(pressedKey);
      });
      mainScene.setOnKeyReleased(keyEvent -> {
        String releasedKey = keyEvent.getCode().toString();
        currentlyActiveKeys.remove(releasedKey);
        if ((releasedKey.equals(RIGHT) && ryuAction == 6) || (releasedKey.equals(LEFT) && ryuAction == 7)) {
          resetDefaultAction();
        }
        sink.next(releasedKey.toLowerCase());
      });
    }, FluxSink.OverflowStrategy.BUFFER).filter(keyEvent -> {
      // accept only the specific key events
      String key = ((String) keyEvent).toUpperCase();
      return key.equals(UP) || key.equals(LEFT) || key.equals(RIGHT) || key.equals(DOWN) || key.equals(PUNCH) || key.equals(KICK);
    }).bufferUntil(keyEvent -> {
      // buffer until Punch button or Kick button is pressed
      //System.out.println("Buffering...");
      return ((String) keyEvent).equals(PUNCH) || ((String) keyEvent).equals(KICK);
    }).publishOn(Schedulers.elastic());

    BaseSubscriber<ArrayList<String>> subscriber = new BaseSubscriber<ArrayList<String>>() {
      private int onNextAmount = 0;

      @Override
      protected void hookOnSubscribe(Subscription subscription) {
        request(2);
      }

      @Override
      protected void hookOnError(Throwable throwable) {
        throwable.printStackTrace();
      }

      @Override
      protected void hookOnComplete() {
      }

      @Override
      protected void hookOnNext(ArrayList<String> commandList) {
        String[] cl = {""};
        commandList.forEach(keyEvent -> cl[0] += (String) keyEvent);
        if (cl[0].contains(SYORYU_CL[0]) || cl[0].contains(SYORYU_CL[1])) {
          resetAction(3, 17, "shouryuuken.mp3");
        } else if (cl[0].contains(HADOU_CL[0]) || cl[0].contains(HADOU_CL[1]) || cl[0].contains(HADOU_CL[2]) || cl[0].contains(HADOU_CL[3])) {
          resetAction(4, 14, "hadouken.mp3");
        } else if (cl[0].contains(TATSUMAKI_CL[0]) || cl[0].contains(TATSUMAKI_CL[1]) || cl[0].contains(TATSUMAKI_CL[2])) {
          resetAction(5, 27, "tatsumaki_senpukyaku.mp3");
        } else if (cl[0].contains(PUNCH) && ryuAction != 1){
          resetAction(1, 8, "punch.mp3");
        } else if (cl[0].contains(KICK) && ryuAction != 2) {
          resetAction(2, 15, "kick.mp3");
        }

        onNextAmount++;
        if (onNextAmount % 2 == 0) {
          request(2);
        }
      }
    };

    flux.subscribe(subscriber);
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

    // walkF[11] 112 x 113 -> 232 x 234
    ryuWalkF = new Image[]{
        new Image(getResource("ryu-walkf-0.gif")),
        new Image(getResource("ryu-walkf-1.gif")),
        new Image(getResource("ryu-walkf-2.gif")),
        new Image(getResource("ryu-walkf-3.gif")),
        new Image(getResource("ryu-walkf-4.gif")),
        new Image(getResource("ryu-walkf-5.gif")),
        new Image(getResource("ryu-walkf-6.gif")),
        new Image(getResource("ryu-walkf-7.gif")),
        new Image(getResource("ryu-walkf-8.gif")),
        new Image(getResource("ryu-walkf-9.gif")),
        new Image(getResource("ryu-walkf-10.gif"))
    };

    // walkB[11] 112 x 113 -> 232 x 234
    ryuWalkB = new Image[]{
        new Image(getResource("ryu-walkb-0.gif")),
        new Image(getResource("ryu-walkb-1.gif")),
        new Image(getResource("ryu-walkb-2.gif")),
        new Image(getResource("ryu-walkb-3.gif")),
        new Image(getResource("ryu-walkb-4.gif")),
        new Image(getResource("ryu-walkb-5.gif")),
        new Image(getResource("ryu-walkb-6.gif")),
        new Image(getResource("ryu-walkb-7.gif")),
        new Image(getResource("ryu-walkb-8.gif")),
        new Image(getResource("ryu-walkb-9.gif")),
        new Image(getResource("ryu-walkb-10.gif"))
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
    return RyuWithReactiveStreamsV3.class.getResource(filename).toString();
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
    graphicsContext.clearRect(RYU_POS_X , MARGIN, KEY_WIDTH *1.5 - MARGIN + STATIC_MARGIN + TATSUMAKI_MARGIN, RYU_POS_Y - MARGIN);
    switch (ryuAction) {
      case 1:
        graphicsContext.drawImage(ryuPunch[mod], RYU_POS_X + STATIC_MARGIN + TATSUMAKI_MARGIN, RYU_POS_Y - 217, 263, 217);
        if (mod == 7) {
          resetDefaultAction();
        }
        break;
      case 2:
        graphicsContext.drawImage(ryuKick[mod], RYU_POS_X + STATIC_MARGIN + TATSUMAKI_MARGIN , RYU_POS_Y - 240, 306, 240);
        if (mod == 14) {
          resetDefaultAction();
        }
        break;
      case 3:
        graphicsContext.drawImage(ryuSyoryuken[mod], RYU_POS_X + STATIC_MARGIN + TATSUMAKI_MARGIN, RYU_POS_Y - 414, 211, 414);
        if (mod == 16) {
          resetDefaultAction();
        }
        break;
      case 4:
        graphicsContext.drawImage(ryuHadou[mod], RYU_POS_X + STATIC_MARGIN + TATSUMAKI_MARGIN, RYU_POS_Y - 215, 282, 215);
        if (mod == 13) {
          resetDefaultAction();
        }
        break;
      case 5:
        graphicsContext.drawImage(ryuTatsumaki[mod], RYU_POS_X , RYU_POS_Y - 290, 329, 290);
        if (mod == 26) {
          resetDefaultAction();
        }
        break;
      case 6:
        graphicsContext.drawImage(ryuWalkF[mod], RYU_POS_X + TATSUMAKI_MARGIN , RYU_POS_Y - 234, 232, 234);
        if (mod == 10) {
          resetDefaultAction();
        }
        break;
      case 7:
        graphicsContext.drawImage(ryuWalkB[mod], RYU_POS_X + TATSUMAKI_MARGIN , RYU_POS_Y - 234, 232, 234);
        if (mod == 10) {
          resetDefaultAction();
        }
        break;
      default:
        graphicsContext.drawImage(ryuKamae[mod], RYU_POS_X + STATIC_MARGIN + TATSUMAKI_MARGIN, RYU_POS_Y - 230, 161, 230);
    }
  }

  private static void ryuVoice(String file) {
    AudioClip clip = new AudioClip(getResource(file));
    clip.play();
  }

  private static void resetDefaultAction() {
    resetAction(0,DEFAULT_FRAME,null);
  }

  private static void resetAction(int action, long frame, String voice) {
    if (voice != null) {
      ryuVoice(voice);
    }
    ryuAction = action;
    stime = 0;
    foreno = -1;
    animationFrame = frame;
  }
}