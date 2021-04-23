package cn.mypro.swing.dao;

import cn.mypro.swing.entity.HVAJapanAVS;
import cn.mypro.utils.DataBaseUtils;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class HVAJapanAVSDao {

    public static final String qrySourcesByMUUIDSql = "select UUID,M_UUID,TRANSLATE,SUBTITLE,QUALITY,WATERMARK,SIZES,FORMAT,UPLOAD_FLAG,UPLOAD_TIME,SOURCE_URI,PASSWORD,CREATE_TIME,UPDATE_TIME,RESOLUTION,BIT_RATE,COMPRESS_FORMAT,SOURCE_NAME from H_V_A_JAP_AV_S where M_UUID = ?";
    public static final String insertSourcesSql = "insert into H_V_A_JAP_AV_S(UUID,M_UUID,TRANSLATE,SUBTITLE,QUALITY,WATERMARK,SIZES,FORMAT,UPLOAD_FLAG,UPLOAD_TIME,SOURCE_URI,PASSWORD,CREATE_TIME,UPDATE_TIME,RESOLUTION,BIT_RATE,COMPRESS_FORMAT,SOURCE_NAME) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    public static final String updateSourcesSql = "update H_V_A_JAP_AV_S set M_UUID = ? ,TRANSLATE = ? ,SUBTITLE = ? ,QUALITY = ? ,WATERMARK = ? ,SIZES = ? ,FORMAT = ? ,UPLOAD_FLAG = ? ,UPLOAD_TIME = ? ,SOURCE_URI = ? ,PASSWORD = ? ,UPDATE_TIME = ? ,RESOLUTION = ? ,BIT_RATE = ? ,COMPRESS_FORMAT = ? ,SOURCE_NAME = ?  where UUID = ?";
    public static final String deleteSourcesSql = "delete from H_V_A_JAP_AV_S where UUID = ?";

    public static List<HVAJapanAVS> selectSourcesByMUUID(Connection connection,String uuid) throws SQLException {
        List<Map<String, Object>> maps = DataBaseUtils.queryMapList(connection, qrySourcesByMUUIDSql, uuid);


        List<HVAJapanAVS> list = new ArrayList<>();
        if (maps == null || maps.size() == 0) return null;

        for (Map<String, Object> map : maps) {

            HVAJapanAVS source = new HVAJapanAVS();

            source.setUuid((String) map.get("UUID"));
            source.setM_uuid((String) map.get("M_UUID"));
            source.setTranslate((String) map.get("TRANSLATE"));
            source.setSubtitle((String) map.get("SUBTITLE"));
            source.setQuality((String) map.get("QUALITY"));
            source.setWatermark((String) map.get("WATERMARK"));
            source.setSizes(((BigDecimal) map.get("SIZES")).longValue());
            source.setFormat((String) map.get("FORMAT"));
            source.setUpload_flag((String) map.get("UPLOAD_FLAG"));
            source.setUpload_time((String) map.get("UPLOAD_TIME"));
            source.setSource_uri((String) map.get("SOURCE_URI"));
            source.setPassword((String) map.get("PASSWORD"));
            source.setCreate_time((String) map.get("CREATE_TIME"));
            source.setUpdate_time((String) map.get("UPDATE_TIME"));
            source.setResolution((String) map.get("RESOLUTION"));
            source.setBit_rate(((BigDecimal) map.get("BIT_RATE")).longValue());
            source.setCompress_format((String) map.get("COMPRESS_FORMAT"));
            source.setSource_name((String) map.get("SOURCE_NAME"));
            list.add(source);
        }

        return list;

    }

    public static void insertSources(Connection connection,HVAJapanAVS hvaJapanAVS) throws SQLException {
        DataBaseUtils.executeUpdate(connection,insertSourcesSql,
                hvaJapanAVS.getUuid(),
                hvaJapanAVS.getM_uuid(),
                hvaJapanAVS.getTranslate(),
                hvaJapanAVS.getSubtitle(),
                hvaJapanAVS.getQuality(),
                hvaJapanAVS.getWatermark(),
                hvaJapanAVS.getSizes(),
                hvaJapanAVS.getFormat(),
                hvaJapanAVS.getUpload_flag(),
                hvaJapanAVS.getUpload_time(),
                hvaJapanAVS.getSource_uri(),
                hvaJapanAVS.getPassword(),
                hvaJapanAVS.getCreate_time(),
                hvaJapanAVS.getUpdate_time(),
                hvaJapanAVS.getResolution(),
                hvaJapanAVS.getBit_rate(),
                hvaJapanAVS.getCompress_format(),
                hvaJapanAVS.getSource_name());
    }

    public static void updateSources(Connection connection,HVAJapanAVS hvaJapanAVS) throws SQLException {
        DataBaseUtils.executeUpdate(connection,updateSourcesSql,
                hvaJapanAVS.getM_uuid(),
                hvaJapanAVS.getTranslate(),
                hvaJapanAVS.getSubtitle(),
                hvaJapanAVS.getQuality(),
                hvaJapanAVS.getWatermark(),
                hvaJapanAVS.getSizes(),
                hvaJapanAVS.getFormat(),
                hvaJapanAVS.getUpload_flag(),
                hvaJapanAVS.getUpload_time(),
                hvaJapanAVS.getSource_uri(),
                hvaJapanAVS.getPassword(),
                hvaJapanAVS.getUpdate_time(),
                hvaJapanAVS.getResolution(),
                hvaJapanAVS.getBit_rate(),
                hvaJapanAVS.getCompress_format(),
                hvaJapanAVS.getSource_name(),
                hvaJapanAVS.getUuid());
    }

    public static void deleteSources(Connection connection,HVAJapanAVS hvaJapanAVS) throws SQLException {
        DataBaseUtils.executeUpdate(connection,deleteSourcesSql,hvaJapanAVS.getUuid());
    }

}
