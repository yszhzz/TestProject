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

    public JTabbedPane initAddNewSourceTab(JFrame jFrame) {
        serviceConn = DataBaseUtils.ensureDataBaseConnection(DbName.LOCAL);
        father = jFrame;
        JTabbedPane child = new JTabbedPane(SwingConstants.TOP, JTabbedPane.WRAP_TAB_LAYOUT);

        HVAJapanAVMView hvaJapanAVMView = new HVAJapanAVMView(serviceConn, father);
        child.addTab("资源操作", new JScrollPane(hvaJapanAVMView.initSelectSourcesJFrame()));

        HVAJapanPersonView hvaJapanPersonView = new HVAJapanPersonView(serviceConn, father);
        child.addTab("添加新男/女优", hvaJapanPersonView.initAddNewPersonJFrame());

        HVAJapanLabelView hvaJapanLabelView = new HVAJapanLabelView(serviceConn, father);
        child.addTab("添加新标签", hvaJapanLabelView.initAddNewLabelJFrame());

        return child;
    }

}
