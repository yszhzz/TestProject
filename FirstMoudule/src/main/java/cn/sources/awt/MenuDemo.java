package cn.sources.awt;

import java.awt.*;
import java.awt.event.*;

public class MenuDemo {

    private Frame frame = new Frame("Menu");



    private MenuBar menuBar = new MenuBar();
    private Menu fileMenu = new Menu("File");
    private Menu editMenu = new Menu("Edit");

    private Menu formatMenu = new Menu("Format");

    private MenuItem auto = new MenuItem("auto");
    private MenuItem copy = new MenuItem("copy");
    private MenuItem paste = new MenuItem("paste");

    private MenuItem comment = new MenuItem("comment",new MenuShortcut(KeyEvent.VK_Q,true));
    private MenuItem cancel = new MenuItem("cancel");

    private TextArea tx = new TextArea("I LOVE\n",6,40);

    private Panel panelX = new Panel();

    private Panel panel = new Panel();

    private PopupMenu popupMenu = new PopupMenu();

    private MenuItem run1 = new MenuItem("run1");
    private MenuItem run2 = new MenuItem("run2");
    private MenuItem run3 = new MenuItem("run3");





    public void init() {
        comment.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //可以通过Event 对象获取事件的详细信息，getActionCommand为获取触发组件的名称
                tx.append(e.getActionCommand()+"\n");
            }
        });

        popupMenu.add(run1);
        popupMenu.add(run2);
        popupMenu.add(run3);

        ActionListener al = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String actionCommand = e.getActionCommand();
                tx.append(actionCommand + "\n");
            }
        };
        run1.addActionListener(al);
        run2.addActionListener(al);
        run3.addActionListener(al);


        formatMenu.add(comment);
        formatMenu.add(cancel);

        editMenu.add(auto);
        editMenu.add(copy);
        editMenu.add(paste);
        editMenu.add(new MenuItem("-"));
        editMenu.add(formatMenu);

        menuBar.add(fileMenu);
        menuBar.add(editMenu);

        panel.add(popupMenu);
        panel.setPreferredSize(new Dimension(400,300));
        //这里也使用了适配器模式，只重写需要重写的方法，其他都是空实现。
        /*
            new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        };
        */
        panel.addMouseListener(new MouseAdapter() {
            //该方法为点击完成
            @Override
            public void mouseReleased(MouseEvent e) {
                //该方法获取是否为右键点击
                boolean popupTrigger = e.isPopupTrigger();
                //如果右键点击，则展示右键菜单，参数为事件源组件，菜单显示的位置，可通过Event对象获取
                //事件源组件一定要与绑定监听器的组件一致，否则会报错
                if (popupTrigger) popupMenu.show(panel,e.getX(),e.getY());
            }
        });

        frame.setMenuBar(menuBar);
        frame.add(tx);
        frame.add(panel,BorderLayout.SOUTH);
        //frame.add(panelX,BorderLayout.NORTH);


        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        new MenuDemo().init();
    }

}
