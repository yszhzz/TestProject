package AddPage.VJA;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
/*
 * Created by JFormDesigner on Tue Feb 23 09:53:45 CST 2021
 */



/**
 * @author ice
 */
public class AddVJAPage extends JFrame {
    public AddVJAPage() {
        initComponents();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        ResourceBundle bundle = ResourceBundle.getBundle("AddPage.VJA.AddVJAPage");
        label1 = new JLabel();

        //======== this ========
        var contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //---- label1 ----
        label1.setText(bundle.getString("AddVJAPage.label1.text"));
        label1.setHorizontalAlignment(SwingConstants.CENTER);
        label1.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 16));
        contentPane.add(label1, BorderLayout.NORTH);
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JLabel label1;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
