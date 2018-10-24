package ru.geekbrains.stargame.utils;



import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class ScoreManager {

    private int stageOld;
    private int frageOld;
    private String bestResult;
    private FileHandle f = Gdx.files.local("records/highscore.txt");

    public ScoreManager() {
        checkScore();

    }

    public String getBestResult() {
        if(stageOld>0)
        return "Your best score is: "+bestResult;
        else return "It is your first game";
    }

    public void SaveScore(int frags, int stage) {
        checkScore();

        try {
            if (stage >= stageOld) {
                if (frags > frageOld) {
                    stageOld=stage;
                    frageOld =frags;
                    //значение false, то текущее содержимое файла будут перезаписано.
                    f.writeString("Frags: " + frags + " Stage: " + stage, false);
                }
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }
  private void checkScore(){
      try {
          bestResult = f.readString();
          if (bestResult != null && bestResult.length() > 16) {
              this.frageOld = Integer.parseInt(bestResult.split("\\s")[1]);
              this.stageOld = Integer.parseInt(bestResult.split("\\s")[3]);
          } else {
              this.stageOld = 0;
              this.frageOld = 0;
          }

      } catch (RuntimeException e) {
          e.printStackTrace();
      }
  }


}