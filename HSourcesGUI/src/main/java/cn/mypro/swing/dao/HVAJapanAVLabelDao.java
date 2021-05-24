package cn.mypro.swing.dao;

import cn.mypro.swing.entity.HVAJapanAVLabelM;
import cn.mypro.swing.entity.HVAJapanAVM;
import cn.mypro.swing.entity.HVAJapanAVPersonM;
import cn.mypro.utils.DataBaseUtils;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class HVAJapanAVLabelDao {

    private static String insertLabelSql = "insert into H_V_A_JAP_AV_LABEL_M(UUID,TYPE_1,TYPE_2,TYPE_3,LABEL_CODE,LABEL_SHOW,LABEL_COMMENT,LABEL_COUNT,LABEL_LEVEL,CREATE_TIME,UPDATE_TIME) values(?,?,?,?,?,?,?,?,?,?,?)";
    private static String selectMaxType3ByType12Sql = "select Max(TYPE_3) from H_V_A_JAP_AV_LABEL_M where TYPE_1 = ? and TYPE_2 = ?";
    private static String selectAllSql = "select UUID,TYPE_1,TYPE_2,TYPE_3,TYPE_4,TYPE_5,LABEL_CODE,LABEL_SHOW,LABEL_COMMENT,LABEL_COUNT,LABEL_LEVEL,CREATE_TIME,UPDATE_TIME from H_V_A_JAP_AV_LABEL_M order by TYPE_1,TYPE_2,LABEL_SHOW";

    private static String selectLabelByMessageUUIDSql = "select a.UUID,a.TYPE_1,a.TYPE_2,a.TYPE_3,a.TYPE_4,a.TYPE_5,a.LABEL_CODE,a.LABEL_SHOW,a.LABEL_COMMENT,a.LABEL_COUNT,a.LABEL_LEVEL,a.CREATE_TIME,a.UPDATE_TIME from H_V_A_JAP_AV_LABEL_M a,H_V_A_JAP_AV_LABEL_C c where a.UUID = c.UUID_AV_LABLE_M and c.UUID_AV_M = ? order by a.TYPE_1,a.TYPE_2,a.LABEL_SHOW";
    private static String selectLabelByShowSql = "select UUID,TYPE_1,TYPE_2,TYPE_3,TYPE_4,TYPE_5,LABEL_CODE,LABEL_SHOW,LABEL_COMMENT,LABEL_COUNT,LABEL_LEVEL,CREATE_TIME,UPDATE_TIME from H_V_A_JAP_AV_LABEL_M where LABEL_SHOW like ? order by TYPE_1,TYPE_2,LABEL_SHOW";

    private static String mergeLabelMessageCSql = "merge into H_V_A_JAP_AV_LABEL_C using dual on (UUID_AV_M = ? and UUID_AV_LABLE_M = ?) WHEN NOT MATCHED THEN insert values(?,?,?)";
    private static String deleteAllByMUUID = "delete from H_V_A_JAP_AV_LABEL_C where UUID_AV_M = ?";

    public static void insertLabel(Connection connection, HVAJapanAVLabelM labelM) throws SQLException {
        DataBaseUtils.executeUpdate(connection,insertLabelSql,
                labelM.getUuid(),
                labelM.getType_1(),
                labelM.getType_2(),
                labelM.getType_3(),
                labelM.getLabel_code(),
                labelM.getLabel_show(),
                labelM.getLabel_comment(),
                labelM.getLabel_count(),
                labelM.getLabel_level(),
                labelM.getCreate_time(),
                labelM.getUpdate_time());
    }

    public static String getNextType3ByType12(Connection connection,String type_1,String type_2) throws SQLException {
        String s = (String) DataBaseUtils.queryOne(connection, selectMaxType3ByType12Sql, 1, type_1, type_2);
        if (s == null) return "1";
        else return Integer.valueOf(s) + 1 + "";

    }

    public static List<HVAJapanAVLabelM> qryAll(Connection connection) throws SQLException {
        List<Map<String, Object>> allLabels = DataBaseUtils.queryMapList(connection, selectAllSql);

        List<HVAJapanAVLabelM> labels = new ArrayList<>();

        for (Map<String, Object> selectLabel : allLabels) {

            HVAJapanAVLabelM label = new HVAJapanAVLabelM();
            label.setUuid((String) selectLabel.get("UUID"));
            label.setType_1((String) selectLabel.get("TYPE_1"));
            label.setType_2((String) selectLabel.get("TYPE_2"));
            label.setType_3((String) selectLabel.get("TYPE_3"));
            label.setType_4((String) selectLabel.get("TYPE_4"));
            label.setType_5((String) selectLabel.get("TYPE_5"));
            label.setLabel_code((String) selectLabel.get("LABEL_CODE"));
            label.setLabel_show((String) selectLabel.get("LABEL_SHOW"));
            label.setLabel_comment((String) selectLabel.get("LABEL_COMMENT"));
            label.setLabel_count(((BigDecimal) selectLabel.get("LABEL_COUNT")).longValue());
            label.setLabel_level((String) selectLabel.get("LABEL_LEVEL"));
            label.setCreate_time((String) selectLabel.get("CREATE_TIME"));
            label.setUpdate_time((String) selectLabel.get("UPDATE_TIME"));
            labels.add(label);
        }

        return labels;
    }

    public static List<HVAJapanAVLabelM> qryLabelByShow(Connection connection,String show) throws SQLException {
        List<Map<String, Object>> allLabels = DataBaseUtils.queryMapList(connection, selectLabelByShowSql,show);

        List<HVAJapanAVLabelM> labels = new ArrayList<>();

        for (Map<String, Object> selectLabel : allLabels) {

            HVAJapanAVLabelM label = new HVAJapanAVLabelM();
            label.setUuid((String) selectLabel.get("UUID"));
            label.setType_1((String) selectLabel.get("TYPE_1"));
            label.setType_2((String) selectLabel.get("TYPE_2"));
            label.setType_3((String) selectLabel.get("TYPE_3"));
            label.setType_4((String) selectLabel.get("TYPE_4"));
            label.setType_5((String) selectLabel.get("TYPE_5"));
            label.setLabel_code((String) selectLabel.get("LABEL_CODE"));
            label.setLabel_show((String) selectLabel.get("LABEL_SHOW"));
            label.setLabel_comment((String) selectLabel.get("LABEL_COMMENT"));
            label.setLabel_count(((BigDecimal) selectLabel.get("LABEL_COUNT")).longValue());
            label.setLabel_level((String) selectLabel.get("LABEL_LEVEL"));
            label.setCreate_time((String) selectLabel.get("CREATE_TIME"));
            label.setUpdate_time((String) selectLabel.get("UPDATE_TIME"));
            labels.add(label);
        }
        return labels;
    }


    public static List<HVAJapanAVLabelM> qryLabelByMessageUUID(Connection connection,String messageUUID) throws SQLException {
        List<Map<String, Object>> allLabels = DataBaseUtils.queryMapList(connection, selectLabelByMessageUUIDSql,messageUUID);

        List<HVAJapanAVLabelM> labels = new ArrayList<>();

        for (Map<String, Object> selectLabel : allLabels) {

            HVAJapanAVLabelM label = new HVAJapanAVLabelM();
            label.setUuid((String) selectLabel.get("UUID"));
            label.setType_1((String) selectLabel.get("TYPE_1"));
            label.setType_2((String) selectLabel.get("TYPE_2"));
            label.setType_3((String) selectLabel.get("TYPE_3"));
            label.setType_4((String) selectLabel.get("TYPE_4"));
            label.setType_5((String) selectLabel.get("TYPE_5"));
            label.setLabel_code((String) selectLabel.get("LABEL_CODE"));
            label.setLabel_show((String) selectLabel.get("LABEL_SHOW"));
            label.setLabel_comment((String) selectLabel.get("LABEL_COMMENT"));
            label.setLabel_count(((BigDecimal) selectLabel.get("LABEL_COUNT")).longValue());
            label.setLabel_level((String) selectLabel.get("LABEL_LEVEL"));
            label.setCreate_time((String) selectLabel.get("CREATE_TIME"));
            label.setUpdate_time((String) selectLabel.get("UPDATE_TIME"));
            labels.add(label);
        }
        return labels;
    }

    public static void mergeLabelMessageC(Connection connection, HVAJapanAVM avm) throws SQLException {
        DataBaseUtils.executeUpdate(connection,deleteAllByMUUID,avm.getUuid());
        if(avm.getLabels() == null) return;
        for (HVAJapanAVLabelM label : avm.getLabels()) {
            DataBaseUtils.executeUpdate(connection,mergeLabelMessageCSql,avm.getUuid(),label.getUuid(),
                    avm.getUuid(),
                    label.getUuid(),
                    new SimpleDateFormat("yyyyMMddHHmmss").format(System.currentTimeMillis()));

        }
    }



}
