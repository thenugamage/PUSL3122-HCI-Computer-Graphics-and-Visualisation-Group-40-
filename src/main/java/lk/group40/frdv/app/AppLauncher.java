package lk.group40.frdv.app;

import lk.group40.frdv.ui.LoginFrame;

public class AppLauncher {

    private AppLauncher() {}

    public static void start() {
        LoginFrame login = new LoginFrame();
        login.setVisible(true);
    }
}
