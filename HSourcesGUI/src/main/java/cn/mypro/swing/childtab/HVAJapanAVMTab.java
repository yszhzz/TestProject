package cn.mypro.swing.childtab;

import cn.mypro.swing.childtab.childview.HVAJapanAVMView;
import cn.mypro.swing.childtab.childview.HVAJapanLabelView;
import cn.mypro.swing.childtab.childview.HVAJapanPersonView;

import cn.mypro.utils.DataBaseUtils;
import cn.mypro.utils.DbName;

import javax.swing.*;

import java.sql.Connection;



public class HVAJapanAVMTab {
    private JFrame father = null;
    private Connection serviceConn = null;

    public JTabbedPane initAddNewSourceTab(JFrame jFrame,JTextArea runMessage) {
        //serviceConn = DataBaseUtils.ensureDataBaseConnection(DbName.LOCAL);
        serviceConn = null;
        father = jFrame;
        JTabbedPane child = new JTabbedPane(SwingConstants.TOP, JTabbedPane.WRAP_TAB_LAYOUT);

        HVAJapanAVMView hvaJapanAVMView = new HVAJapanAVMView(serviceConn, father, runMessage);
        child.addTab("资源操作", new JScrollPane(hvaJapanAVMView.initTab()));

        HVAJapanPersonView hvaJapanPersonView = new HVAJapanPersonView(serviceConn, father, runMessage);
        child.addTab("添加新男/女优", new JScrollPane(hvaJapanPersonView.initTab()));

        HVAJapanLabelView hvaJapanLabelView = new HVAJapanLabelView(serviceConn, father, runMessage);
        child.addTab("添加新标签", hvaJapanLabelView.initTab());

        return child;
    }

}
