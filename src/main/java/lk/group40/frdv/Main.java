package lk.group40.frdv;

import javax.swing.SwingUtilities;
import lk.group40.frdv.app.AppLauncher;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(AppLauncher::start);
    }
}
