package cn.mypro.swing.entity;

import lombok.Getter;
import lombok.Setter;

import java.text.SimpleDateFormat;
import java.util.UUID;

@Getter
@Setter
public class HVAJapanAVS {

    private String uuid;
    private String m_uuid;
    private String translate;
    private String subtitle;
    private String quality;
    private String watermark;
    private long sizes;
    private String format;
    private String upload_flag;
    private String upload_time;
    private String source_uri;
    private String password;
    private String create_time;
    private String update_time;
    private String resolution;
    private long bit_rate;
    private String compress_format;
    private String source_name;

    public HVAJapanAVS() {
    }

    public void setValuesAsNormal() {
        uuid = UUID.randomUUID().toString().replace("-", "");
    }

    public void readByFile(String filePath) {

    }

    @Override
    public String toString() {
        return "HVAJapanAVS{" +
                "uuid='" + uuid + '\'' +
                ", m_uuid='" + m_uuid + '\'' +
                ", translate='" + translate + '\'' +
                ", subtitle='" + subtitle + '\'' +
                ", quality='" + quality + '\'' +
                ", watermark='" + watermark + '\'' +
                ", sizes=" + sizes +
                ", format='" + format + '\'' +
                ", upload_flag='" + upload_flag + '\'' +
                ", upload_time='" + upload_time + '\'' +
                ", source_uri='" + source_uri + '\'' +
                ", password='" + password + '\'' +
                ", create_time='" + create_time + '\'' +
                ", update_time='" + update_time + '\'' +
                ", resolution='" + resolution + '\'' +
                ", bit_rate='" + bit_rate + '\'' +
                ", compress_format='" + compress_format + '\'' +
                ", source_name='" + source_name + '\'' +
                '}';
    }
}
