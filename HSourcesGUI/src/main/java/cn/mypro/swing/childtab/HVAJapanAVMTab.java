package cn.mypro.swing.childtab;

import cn.mypro.swing.childtab.childview.HVAJapanAVMView;
import cn.mypro.swing.childtab.childview.HVAJapanLabelView;
import cn.mypro.swing.childtab.childview.HVAJapanPersonView;

import cn.mypro.swing.util.webmagic.WebMagicOfPersonsUtil;
import cn.mypro.utils.DataBaseUtils;
import cn.mypro.utils.DbName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

import java.sql.Connection;



public class HVAJapanAVMTab {

    private static Logger logger = LoggerFactory.getLogger(HVAJapanAVMTab.class);

    private JFrame father = null;
    private Connection serviceConn = null;

    public JTabbedPane initAddNewSourceTab(JFrame jFrame,JTextArea runMessage) {
        serviceConn = DataBaseUtils.ensureDataBaseConnection(DbName.LOCAL);
        //serviceConn = null;
        father = jFrame;
        JTabbedPane child = new JTabbedPane(SwingConstants.TOP, JTabbedPane.WRAP_TAB_LAYOUT);

        logger.info("初始化[资源操作界面]");
        HVAJapanAVMView hvaJapanAVMView = new HVAJapanAVMView(serviceConn, father, runMessage);
        child.addTab("资源操作界面", new JScrollPane(hvaJapanAVMView.initTab()));
        logger.info("初始化[人物操作界面]");
        HVAJapanPersonView hvaJapanPersonView = new HVAJapanPersonView(serviceConn, father, runMessage);
        child.addTab("人物操作界面", new JScrollPane(hvaJapanPersonView.initTab()));
        logger.info("初始化[标签操作界面]");
        HVAJapanLabelView hvaJapanLabelView = new HVAJapanLabelView(serviceConn, father, runMessage);
        child.addTab("标签操作界面", hvaJapanLabelView.initTab());

        return child;
    }

}
