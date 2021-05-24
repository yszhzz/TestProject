package cn.mypro.swing.childtab;

import cn.mypro.swing.childtab.childview.HVAJapanAVMView;
import cn.mypro.swing.childtab.childview.HVAJapanLabelView;
import cn.mypro.swing.childtab.childview.HVAJapanPersonView;
import cn.mypro.swing.constant.LabelConstant;
import cn.mypro.swing.dao.HVAJapanAVLabelDao;
import cn.mypro.swing.dao.HVAJapanAVMDao;
import cn.mypro.swing.dao.HVAJapanAVPersonDao;
import cn.mypro.swing.dao.HVAJapanAVSDao;
import cn.mypro.swing.entity.HVAJapanAVLabelM;
import cn.mypro.swing.entity.HVAJapanAVM;
import cn.mypro.swing.entity.HVAJapanAVPersonM;
import cn.mypro.swing.entity.HVAJapanAVS;
import cn.mypro.swing.util.BaiduTranslateUtil;
import cn.mypro.swing.util.webmagic.WebMagicUtil;
import cn.mypro.utils.DataBaseUtils;
import cn.mypro.utils.DbName;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;
import lombok.SneakyThrows;

import javax.imageio.ImageIO;
import javax.lang.model.element.VariableElement;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLOutput;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.function.Predicate;


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
