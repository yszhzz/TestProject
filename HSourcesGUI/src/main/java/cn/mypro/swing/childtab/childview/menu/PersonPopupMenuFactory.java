package cn.mypro.swing.childtab.childview.menu;

import cn.mypro.swing.childtab.JChildTabView;
import cn.mypro.swing.childtab.childview.HVAJapanPersonView;
import cn.mypro.swing.constant.LabelConstant;
import cn.mypro.swing.constant.PublicVariablePool;
import cn.mypro.swing.entity.HVAJapanAVPersonM;
import cn.mypro.swing.util.file.WindowClipboardUtil;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class PersonPopupMenuFactory {

    private JPopupMenu textPopupMenu = null;
    private JMenuItem jumpItem = null;
    private JMenuItem copyNameItem = null;
    private JMenuItem copyUUIDItem = null;

    private JList<HVAJapanAVPersonM> jList = null;

    public PersonPopupMenuFactory(JList<HVAJapanAVPersonM> jList) {
        this.jList = jList;

        textPopupMenu = new JPopupMenu();
        jumpItem = new JMenuItem("跳转至人物信息",new ImageIcon("HSourcesGUI/src/main/resources/pic/new.png"));
        copyNameItem = new JMenuItem("复制人物名称",new ImageIcon("HSourcesGUI/src/main/resources/pic/new.png"));
        copyUUIDItem = new JMenuItem("复制人物唯一标识",new ImageIcon("HSourcesGUI/src/main/resources/pic/new.png"));
    }

    public JPopupMenu initMenu() {


        //绑定右键按钮
        jumpItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //获取人物页面 且 跳转
                JTabbedPane avTab = (JTabbedPane) PublicVariablePool.getComponentValue("AVTab");

                HVAJapanAVPersonM selectedValue = jList.getSelectedValue();
                if (selectedValue != null) {
                    HVAJapanPersonView personAVTab = (HVAJapanPersonView) PublicVariablePool.getTabsValue("PersonAVTab");
                    personAVTab.showPerson(selectedValue.getUuid());
                    personAVTab.cleanPersonResultSelected();
                    avTab.setSelectedIndex(PublicVariablePool.getIntegerValue("PersonTabOfAVTabIndex"));
                } else {
                    PublicVariablePool.runfatherLogMessagePrint("请选择人物进行跳转！",LabelConstant.TEXT_APPEND_MODEL_TIME_AND_NEXT);
                }
            }
        });

        copyNameItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = "";
                HVAJapanAVPersonM selectedValue = jList.getSelectedValue();
                if (selectedValue != null) text = selectedValue.getNames();
                WindowClipboardUtil.setIntoClipboard(text);
            }
        });
        copyUUIDItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = "";
                HVAJapanAVPersonM selectedValue = jList.getSelectedValue();
                if (selectedValue != null) text = selectedValue.getUuid();
                WindowClipboardUtil.setIntoClipboard(text);
            }
        });

        textPopupMenu.add(jumpItem);
        textPopupMenu.add(copyNameItem);
        textPopupMenu.add(copyUUIDItem);

        return textPopupMenu;
    }


    public static JPopupMenu createPopupMenu(JList<HVAJapanAVPersonM> jList) {
        return new PersonPopupMenuFactory(jList).initMenu();
    }


}
