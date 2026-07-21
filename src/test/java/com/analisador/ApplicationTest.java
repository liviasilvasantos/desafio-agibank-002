package com.analisador;

import org.junit.jupiter.api.Test;

public class ApplicationTest {

    @Test
    void deveIniciarEPararComInterrupcao() throws Exception {
        final Thread thread = new Thread(() -> Application.main(new String[]{}));
        thread.setDaemon(true);
        thread.start();

        Thread.sleep(1000);

        thread.interrupt();
        thread.join(3000);
    }
}
