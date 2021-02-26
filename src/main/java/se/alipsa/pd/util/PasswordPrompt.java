package se.alipsa.pd.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class PasswordPrompt {

  static class EraserThread implements Runnable {
    private boolean stop;

    /**
     *@param prompt The prompt displayed to the user
     */
    public EraserThread(String prompt) {
      System.out.print(prompt);
    }

    /**
     * Begin masking...display asterisks (*)
     */
    public void run () {
      stop = true;
      while (stop) {
        System.out.print("\010*");
        try {
          Thread.currentThread().sleep(1);
        } catch(InterruptedException ie) {
          ie.printStackTrace();
        }
      }
    }

    /**
     * Instruct the thread to stop masking
     */
    public void stopMasking() {
      this.stop = false;
    }
  }

  /**
   *@param prompt The prompt to display to the user
   *@return The password as entered by the user
   */
  public static String readPassword (String prompt) throws IOException {
    EraserThread et = new EraserThread(prompt);
    Thread mask = new Thread(et);
    mask.start();

    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    String password = "";

    password = in.readLine();

    // stop masking
    et.stopMasking();
    // return the password entered by the user
    return password;
  }
}
