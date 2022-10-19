package quitada;

import java.io.File;
import javafx.application.Application;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

public class SoundTest extends Application {
  public static void main(String[] args) throws Exception {
    AudioClip clip = new AudioClip(getResource("shouryuuken.mp3"));
    //clip.setCycleCount(AudioClip.INDEFINITE);
    clip.setCycleCount(1);
    //clip.rateProperty(1.0);
    System.out.println("volume=" + clip.getVolume());
    System.out.println("balance=" + clip.getBalance());
    System.out.println("rate=" + clip.getRate());
    System.out.println("pan=" + clip.getPan());
    System.out.println("priority=" + clip.getPriority());
    clip.setRate(0.99);
    //clip.play(1.0, 0.0, 0.99, 1.0, 100);
    //launch(args);
    clip.play();

    Thread.sleep(1000);

  }
  @Override
  public void start(Stage primaryStage) throws Exception {
    primaryStage.show();
    Media m = new Media(getResource("shouryuuken.mp3"));

    //音声の再生等の操作を実行できるオブジェクト
    MediaPlayer mp = new MediaPlayer(m);
      mp.setCycleCount(MediaPlayer.INDEFINITE);
    mp.play();

    Thread.sleep(10000);
  }

  private static String getResource(String filename) {
    return SoundTest.class.getResource(filename).toString();
  }


}
