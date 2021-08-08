package cn.mypro.swing.childtab.childview.menu;

import cn.mypro.swing.constant.LabelConstant;
import cn.mypro.swing.util.file.WindowClipboardUtil;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TextPopupMenuFactory {

    private JPopupMenu textPopupMenu = null;
    private JMenuItem copyItem = null;
    private JMenuItem pasteItem = null;


    private String textValue;
    private JTextField jTextField;
    private JTextArea jTextArea;

    public TextPopupMenuFactory(String textValue, Object jText) {
        this.textValue = textValue;
        if (textValue.equals(LabelConstant.TEXT_POPUPMENU_TYPE_FIELD)) {
            this.jTextField = (JTextField) jText;
        } else if (textValue.equals(LabelConstant.TEXT_POPUPMENU_TYPE_AREA)) {
            this.jTextArea = (JTextArea) jText;
        } else {
            throw new RuntimeException("该文本类型有误！！");
        }

        textPopupMenu = new JPopupMenu();
        copyItem = new JMenuItem("复制",new ImageIcon("HSourcesGUI/src/main/resources/pic/new.png"));
        pasteItem = new JMenuItem("粘贴",new ImageIcon("HSourcesGUI/src/main/resources/pic/new.png"));

    }

    public JPopupMenu initMenu() {


        //绑定右键按钮
        copyItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = "";
                if (textValue.equals(LabelConstant.TEXT_POPUPMENU_TYPE_FIELD)) {
                    text = jTextField.getSelectedText();
                } else if (textValue.equals(LabelConstant.TEXT_POPUPMENU_TYPE_AREA)) {
                    text = jTextArea.getSelectedText();
                }
                WindowClipboardUtil.setIntoClipboard(text);
            }
        });

        pasteItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = WindowClipboardUtil.getFromClipboard();

                if (textValue.equals(LabelConstant.TEXT_POPUPMENU_TYPE_FIELD)) {
                    int selectionStart = jTextField.getSelectionStart();
                    int selectionEnd = jTextField.getSelectionEnd();
                    jTextField.replaceSelection(text);

                } else if (textValue.equals(LabelConstant.TEXT_POPUPMENU_TYPE_AREA)) {
                    int selectionStart = jTextArea.getSelectionStart();
                    int selectionEnd = jTextArea.getSelectionEnd();
                    //jTextArea.replaceSelection(text);
                    jTextArea.replaceRange(text,selectionStart,selectionEnd);

                    /* 指定光标处输入
                    int dot = jTextArea.getCaret().getDot();
                    thisSource.insert(text,dot);*/
                }
            }
        });


        textPopupMenu.add(copyItem);
        textPopupMenu.add(pasteItem);
        //describe.setComponentPopupMenu(textPopupMenu);




        return textPopupMenu;
    }

    public static JPopupMenu createPopupMenu(String textValue, Object jText) {
        return new TextPopupMenuFactory(textValue,jText).initMenu();
    }

}
