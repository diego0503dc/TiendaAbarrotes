package tiendaabarrotes;

import com.tienda.vistas.LoginFrame;

public class TiendaAbarrotes {

    public static void main(String[] args) {
        // Esto asegura que la ventana se dibuje suavemente
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new LoginFrame().setVisible(true);
            }
        });
    }
}
