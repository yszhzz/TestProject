package cn.mypro.swing.dao;

import cn.mypro.swing.entity.HVAJapanAVLabelM;
import cn.mypro.swing.entity.HVAJapanAVM;
import cn.mypro.swing.entity.HVAJapanAVPersonM;
import cn.mypro.swing.entity.HVAJapanAVS;
import cn.mypro.utils.DataBaseUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class HVAJapanAVMDao {

    public static final String qryAllMessageByCodeNameSql = "select UUID,IF_CODE,ONAME,CNAME,COVER,LANGUAGES,PRODUCTION_COMPANY,PUBLISH_COMPANY,PUBLISH_TIME,SERIES,MOSAIC,DURATION,DESCRIBE,SCORE,RECOMMEND,CUT1,CUT2,CUT3 from H_V_A_JAP_AV_M order by IF_CODE";
    public static final String qryMessageByCodeNameSql = "select UUID,IF_CODE,ONAME,CNAME,COVER,LANGUAGES,PRODUCTION_COMPANY,PUBLISH_COMPANY,PUBLISH_TIME,SERIES,MOSAIC,DURATION,DESCRIBE,SCORE,RECOMMEND,CUT1,CUT2,CUT3 from H_V_A_JAP_AV_M where IF_CODE like ? or ONAME like ? or CNAME like ? order by IF_CODE";
    public static final String qryMessageByLabelUUIDSql = "select UUID,IF_CODE,ONAME,CNAME,COVER,LANGUAGES,PRODUCTION_COMPANY,PUBLISH_COMPANY,PUBLISH_TIME,SERIES,MOSAIC,DURATION,DESCRIBE,SCORE,RECOMMEND,CUT1,CUT2,CUT3 from H_V_A_JAP_AV_M a, H_V_A_JAP_AV_LABEL_C c where a.UUID = c.UUID_AV_M and c.UUID_AV_LABLE_M = ? order by IF_CODE";
    public static final String qryMessageByPersonUUIDSql = "select UUID,IF_CODE,ONAME,CNAME,COVER,LANGUAGES,PRODUCTION_COMPANY,PUBLISH_COMPANY,PUBLISH_TIME,SERIES,MOSAIC,DURATION,DESCRIBE,SCORE,RECOMMEND,CUT1,CUT2,CUT3 from H_V_A_JAP_AV_M a, H_V_A_JAP_AV_PERSON_C c where a.UUID = c.UUID_AV_M and c.UUID_AV_PERSON_M = ? order by IF_CODE";

    public static final String qryMessageByUUIDSql = "select UUID,IF_CODE,ONAME,CNAME,COVER,LANGUAGES,PRODUCTION_COMPANY,PUBLISH_COMPANY,PUBLISH_TIME,SERIES,MOSAIC,DURATION,DESCRIBE,SCORE,RECOMMEND,CUT1,CUT2,CUT3 from H_V_A_JAP_AV_M where UUID = ?";

    public static final String insertMessageSql = "insert into H_V_A_JAP_AV_M(UUID,IF_CODE,ONAME,CNAME,COVER,LANGUAGES,PRODUCTION_COMPANY,PUBLISH_COMPANY,PUBLISH_TIME,SERIES,MOSAIC,DURATION,DESCRIBE,SCORE,RECOMMEND,CUT1,CUT2,CUT3) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    public static final String updateMessageSql = "update H_V_A_JAP_AV_M set IF_CODE = ?,ONAME = ?,CNAME = ?,COVER = ?,LANGUAGES = ?,PRODUCTION_COMPANY = ?,PUBLISH_COMPANY = ?,PUBLISH_TIME = ?,SERIES = ?,MOSAIC = ?,DURATION = ?,DESCRIBE = ?,SCORE = ?,RECOMMEND = ?,CUT1 = ?,CUT2 = ?,CUT3 = ? where UUID = ?";
    public static final String deleteMessageSql = "delete from H_V_A_JAP_AV_M where UUID = ?";

    public static HVAJapanAVM selectMessageByUUID(Connection connection, String uuid) throws SQLException, IOException {
        Map<String, Object> map = DataBaseUtils.queryMap(connection, qryMessageByUUIDSql, uuid);

        if (map == null || map.size() == 0) return null;


        HVAJapanAVM message = new HVAJapanAVM();

        message.setUuid((String) map.get("UUID"));
        message.setIf_Code((String) map.get("IF_CODE"));
        message.setOName((String) map.get("ONAME"));
        message.setCName((String) map.get("CNAME"));
        message.setCover(((Blob) map.get("COVER")).getBinaryStream().readAllBytes());
        message.setLanguages((String) map.get("LANGUAGES"));
        message.setProduction_company((String) map.get("PRODUCTION_COMPANY"));
        message.setPublish_company((String) map.get("PUBLISH_COMPANY"));
        message.setPublish_time((String) map.get("PUBLISH_TIME"));
        message.setSeries((String) map.get("SERIES"));
        message.setMosaic((String) map.get("MOSAIC"));
        message.setDuration(((BigDecimal) map.get("DURATION")).longValue());
        message.setDescribe((String) map.get("DESCRIBE"));
        message.setScore(((BigDecimal) map.get("SCORE")).longValue());
        message.setRecommend((String) map.get("RECOMMEND"));
        message.setCut1(((Blob) map.get("CUT1")).getBinaryStream().readAllBytes());
        message.setCut2(((Blob) map.get("CUT2")).getBinaryStream().readAllBytes());
        message.setCut3(((Blob) map.get("CUT3")).getBinaryStream().readAllBytes());

        List<HVAJapanAVS> hvaJapanAVS = HVAJapanAVSDao.selectSourcesByMUUID(connection, message.getUuid());
        message.setSources(hvaJapanAVS);
        List<HVAJapanAVPersonM> hvaJapanAVPersonMS = HVAJapanAVPersonDao.qryPersonByMessageUUID(connection, message.getUuid());
        message.setPersons(hvaJapanAVPersonMS);
        List<HVAJapanAVLabelM> hvaJapanAVLabelMS = HVAJapanAVLabelDao.qryLabelByMessageUUID(connection, message.getUuid());
        message.setLabels(hvaJapanAVLabelMS);

        return message;
    }

    public static List<HVAJapanAVM> selectAllMessage(Connection connection) throws SQLException, IOException {
        List<Map<String, Object>> maps = DataBaseUtils.queryMapList(connection, qryAllMessageByCodeNameSql);


        if (maps == null || maps.size() == 0) return null;

        List<HVAJapanAVM> list = new ArrayList<>();
        for (Map<String, Object> map : maps) {
            HVAJapanAVM message = new HVAJapanAVM();

            message.setUuid((String) map.get("UUID"));
            message.setIf_Code((String) map.get("IF_CODE"));
            message.setOName((String) map.get("ONAME"));
            message.setCName((String) map.get("CNAME"));
            message.setCover(((Blob) map.get("COVER")).getBinaryStream().readAllBytes());
            message.setLanguages((String) map.get("LANGUAGES"));
            message.setProduction_company((String) map.get("PRODUCTION_COMPANY"));
            message.setPublish_company((String) map.get("PUBLISH_COMPANY"));
            message.setPublish_time((String) map.get("PUBLISH_TIME"));
            message.setSeries((String) map.get("SERIES"));
            message.setMosaic((String) map.get("MOSAIC"));
            message.setDuration(((BigDecimal) map.get("DURATION")).longValue());
            message.setDescribe((String) map.get("DESCRIBE"));
            message.setScore(((BigDecimal) map.get("SCORE")).longValue());
            message.setRecommend((String) map.get("RECOMMEND"));
            message.setCut1(((Blob) map.get("CUT1")).getBinaryStream().readAllBytes());
            message.setCut2(((Blob) map.get("CUT2")).getBinaryStream().readAllBytes());
            message.setCut3(((Blob) map.get("CUT3")).getBinaryStream().readAllBytes());

            List<HVAJapanAVS> hvaJapanAVS = HVAJapanAVSDao.selectSourcesByMUUID(connection, message.getUuid());
            message.setSources(hvaJapanAVS);
            List<HVAJapanAVPersonM> hvaJapanAVPersonMS = HVAJapanAVPersonDao.qryPersonByMessageUUID(connection, message.getUuid());
            message.setPersons(hvaJapanAVPersonMS);
            List<HVAJapanAVLabelM> hvaJapanAVLabelMS = HVAJapanAVLabelDao.qryLabelByMessageUUID(connection, message.getUuid());
            message.setLabels(hvaJapanAVLabelMS);
            list.add(message);
        }
        return list;

    }

    public static List<HVAJapanAVM> selectMessageByCodeName(Connection connection, String text) throws SQLException, IOException {
        List<Map<String, Object>> maps = DataBaseUtils.queryMapList(connection, qryMessageByCodeNameSql, text);


        if (maps == null || maps.size() == 0) return null;

        List<HVAJapanAVM> list = new ArrayList<>();
        for (Map<String, Object> map : maps) {
            HVAJapanAVM message = new HVAJapanAVM();

            message.setUuid((String) map.get("UUID"));
            message.setIf_Code((String) map.get("IF_CODE"));
            message.setOName((String) map.get("ONAME"));
            message.setCName((String) map.get("CNAME"));
            message.setCover(((Blob) map.get("COVER")).getBinaryStream().readAllBytes());
            message.setLanguages((String) map.get("LANGUAGES"));
            message.setProduction_company((String) map.get("PRODUCTION_COMPANY"));
            message.setPublish_company((String) map.get("PUBLISH_COMPANY"));
            message.setPublish_time((String) map.get("PUBLISH_TIME"));
            message.setSeries((String) map.get("SERIES"));
            message.setMosaic((String) map.get("MOSAIC"));
            message.setDuration(((BigDecimal) map.get("DURATION")).longValue());
            message.setDescribe((String) map.get("DESCRIBE"));
            message.setScore(((BigDecimal) map.get("SCORE")).longValue());
            message.setRecommend((String) map.get("RECOMMEND"));
            message.setCut1(((Blob) map.get("CUT1")).getBinaryStream().readAllBytes());
            message.setCut2(((Blob) map.get("CUT2")).getBinaryStream().readAllBytes());
            message.setCut3(((Blob) map.get("CUT3")).getBinaryStream().readAllBytes());

            List<HVAJapanAVS> hvaJapanAVS = HVAJapanAVSDao.selectSourcesByMUUID(connection, message.getUuid());
            message.setSources(hvaJapanAVS);
            List<HVAJapanAVPersonM> hvaJapanAVPersonMS = HVAJapanAVPersonDao.qryPersonByMessageUUID(connection, message.getUuid());
            message.setPersons(hvaJapanAVPersonMS);
            List<HVAJapanAVLabelM> hvaJapanAVLabelMS = HVAJapanAVLabelDao.qryLabelByMessageUUID(connection, message.getUuid());
            message.setLabels(hvaJapanAVLabelMS);
            list.add(message);
        }
        return list;

    }

    public static List<HVAJapanAVM> selectMessageByLabelUUID(Connection connection, String labelUUID) throws SQLException, IOException {
        List<Map<String, Object>> maps = DataBaseUtils.queryMapList(connection, qryMessageByLabelUUIDSql, labelUUID);


        if (maps == null || maps.size() == 0) return null;

        List<HVAJapanAVM> list = new ArrayList<>();
        for (Map<String, Object> map : maps) {
            HVAJapanAVM message = new HVAJapanAVM();

            message.setUuid((String) map.get("UUID"));
            message.setIf_Code((String) map.get("IF_CODE"));
            message.setOName((String) map.get("ONAME"));
            message.setCName((String) map.get("CNAME"));
            message.setCover(((Blob) map.get("COVER")).getBinaryStream().readAllBytes());
            message.setLanguages((String) map.get("LANGUAGES"));
            message.setProduction_company((String) map.get("PRODUCTION_COMPANY"));
            message.setPublish_company((String) map.get("PUBLISH_COMPANY"));
            message.setPublish_time((String) map.get("PUBLISH_TIME"));
            message.setSeries((String) map.get("SERIES"));
            message.setMosaic((String) map.get("MOSAIC"));
            message.setDuration(((BigDecimal) map.get("DURATION")).longValue());
            message.setDescribe((String) map.get("DESCRIBE"));
            message.setScore(((BigDecimal) map.get("SCORE")).longValue());
            message.setRecommend((String) map.get("RECOMMEND"));
            message.setCut1(((Blob) map.get("CUT1")).getBinaryStream().readAllBytes());
            message.setCut2(((Blob) map.get("CUT2")).getBinaryStream().readAllBytes());
            message.setCut3(((Blob) map.get("CUT3")).getBinaryStream().readAllBytes());

            List<HVAJapanAVS> hvaJapanAVS = HVAJapanAVSDao.selectSourcesByMUUID(connection, message.getUuid());
            message.setSources(hvaJapanAVS);
            List<HVAJapanAVPersonM> hvaJapanAVPersonMS = HVAJapanAVPersonDao.qryPersonByMessageUUID(connection, message.getUuid());
            message.setPersons(hvaJapanAVPersonMS);
            List<HVAJapanAVLabelM> hvaJapanAVLabelMS = HVAJapanAVLabelDao.qryLabelByMessageUUID(connection, message.getUuid());
            message.setLabels(hvaJapanAVLabelMS);
            list.add(message);
        }
        return list;

    }

    public static List<HVAJapanAVM> selectMessageByPersonUUID(Connection connection, String personUUID) throws SQLException, IOException {
        List<Map<String, Object>> maps = DataBaseUtils.queryMapList(connection, qryMessageByPersonUUIDSql, personUUID);


        if (maps == null || maps.size() == 0) return null;

        List<HVAJapanAVM> list = new ArrayList<>();
        for (Map<String, Object> map : maps) {
            HVAJapanAVM message = new HVAJapanAVM();

            message.setUuid((String) map.get("UUID"));
            message.setIf_Code((String) map.get("IF_CODE"));
            message.setOName((String) map.get("ONAME"));
            message.setCName((String) map.get("CNAME"));
            message.setCover(((Blob) map.get("COVER")).getBinaryStream().readAllBytes());
            message.setLanguages((String) map.get("LANGUAGES"));
            message.setProduction_company((String) map.get("PRODUCTION_COMPANY"));
            message.setPublish_company((String) map.get("PUBLISH_COMPANY"));
            message.setPublish_time((String) map.get("PUBLISH_TIME"));
            message.setSeries((String) map.get("SERIES"));
            message.setMosaic((String) map.get("MOSAIC"));
            message.setDuration(((BigDecimal) map.get("DURATION")).longValue());
            message.setDescribe((String) map.get("DESCRIBE"));
            message.setScore(((BigDecimal) map.get("SCORE")).longValue());
            message.setRecommend((String) map.get("RECOMMEND"));
            message.setCut1(((Blob) map.get("CUT1")).getBinaryStream().readAllBytes());
            message.setCut2(((Blob) map.get("CUT2")).getBinaryStream().readAllBytes());
            message.setCut3(((Blob) map.get("CUT3")).getBinaryStream().readAllBytes());

            List<HVAJapanAVS> hvaJapanAVS = HVAJapanAVSDao.selectSourcesByMUUID(connection, message.getUuid());
            message.setSources(hvaJapanAVS);
            List<HVAJapanAVPersonM> hvaJapanAVPersonMS = HVAJapanAVPersonDao.qryPersonByMessageUUID(connection, message.getUuid());
            message.setPersons(hvaJapanAVPersonMS);
            List<HVAJapanAVLabelM> hvaJapanAVLabelMS = HVAJapanAVLabelDao.qryLabelByMessageUUID(connection, message.getUuid());
            message.setLabels(hvaJapanAVLabelMS);
            list.add(message);
        }
        return list;

    }

    public static List<HVAJapanAVM> selectMessageByAll(Connection connection, String selectText) throws SQLException, IOException {
        List<HVAJapanAVM> hvaJapanAVMS = selectMessageByCodeName(connection, selectText);
        List<HVAJapanAVM> hvaJapanAVMS1 = selectMessageByLabel(connection, selectText);
        List<HVAJapanAVM> hvaJapanAVMS2 = selectMessageByPerson(connection, selectText);

        return mergeList(hvaJapanAVMS,mergeList(hvaJapanAVMS1,hvaJapanAVMS2));

    }

    public static List<HVAJapanAVM> selectMessageByLabel(Connection connection, String selectText) throws SQLException, IOException {
        List<HVAJapanAVLabelM> hvaJapanAVLabelMS = HVAJapanAVLabelDao.qryLabelByShow(connection, selectText);
        List<HVAJapanAVM> list= new ArrayList<>();
        for (HVAJapanAVLabelM label : hvaJapanAVLabelMS) {
            String uuid = label.getUuid();
            List<HVAJapanAVM> hvaJapanAVMS = selectMessageByLabelUUID(connection, uuid);
            list = mergeList(list,hvaJapanAVMS);
        }

        return list;
    }

    public static List<HVAJapanAVM> selectMessageByPerson(Connection connection, String selectText) throws IOException, SQLException {
        List<HVAJapanAVPersonM> hvaJapanAVPersonMS = HVAJapanAVPersonDao.qryPersonByName(connection, selectText);
        List<HVAJapanAVM> list= new ArrayList<>();
        for (HVAJapanAVPersonM person : hvaJapanAVPersonMS) {
            String uuid = person.getUuid();
            List<HVAJapanAVM> hvaJapanAVMS = selectMessageByPersonUUID(connection, uuid);
            list = mergeList(list,hvaJapanAVMS);
        }

        return list;
    }

    public static List<HVAJapanAVM> mergeList(List<HVAJapanAVM> list1 , List<HVAJapanAVM> list2) {

        if (list1 == null || list1.size() == 0) return list2;
        if (list2 == null || list2.size() == 0) return list1;

        cyc1:for (HVAJapanAVM av1 : list1) {
            cyc2:for (HVAJapanAVM av2 : list2) {
                if (av2.getUuid().equals(av1.getUuid())) continue cyc1;
            }
            list2.add(av1);
        }
        return list2;
    }

    public static void insertMessage(Connection connection,HVAJapanAVM av) throws SQLException {
        av.setUuid(UUID.randomUUID().toString().replace("-",""));

        DataBaseUtils.executeUpdate(connection,insertMessageSql,
                av.getUuid(),
                av.getIf_Code(),
                av.getOName(),
                av.getCName(),
                av.getCover(),
                av.getLanguages(),
                av.getProduction_company(),
                av.getPublish_company(),
                av.getPublish_time(),
                av.getSeries(),
                av.getMosaic(),
                av.getDuration(),
                av.getDescribe(),
                av.getScore(),
                av.getRecommend(),
                av.getCut1(),
                av.getCut2(),
                av.getCut3());

        HVAJapanAVLabelDao.mergeLabelMessageC(connection,av);
        HVAJapanAVPersonDao.mergePersonMessageC(connection,av);
    }

    public static void updateMessage(Connection connection,HVAJapanAVM av) throws SQLException {

        DataBaseUtils.executeUpdate(connection,updateMessageSql,
                av.getIf_Code(),
                av.getOName(),
                av.getCName(),
                av.getCover(),
                av.getLanguages(),
                av.getProduction_company(),
                av.getPublish_company(),
                av.getPublish_time(),
                av.getSeries(),
                av.getMosaic(),
                av.getDuration(),
                av.getDescribe(),
                av.getScore(),
                av.getRecommend(),
                av.getCut1(),
                av.getCut2(),
                av.getCut3(),
                av.getUuid());
        HVAJapanAVLabelDao.mergeLabelMessageC(connection,av);
        HVAJapanAVPersonDao.mergePersonMessageC(connection,av);
    }

    public static void deleteMessage(Connection connection,HVAJapanAVM av) throws SQLException {
        DataBaseUtils.executeUpdate(connection,deleteMessageSql,av.getUuid());
    }
}
