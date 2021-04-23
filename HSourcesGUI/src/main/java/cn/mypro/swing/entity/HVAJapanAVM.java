package cn.mypro.swing.entity;

import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.lf5.viewer.LogTable;

import java.util.List;

@Getter
@Setter
public class HVAJapanAVM {
    private String uuid;
    private String if_Code;
    private String oName;
    private String cName;
    private byte[] cover;
    private String languages;
    private String production_company;
    private String publish_company;
    private String publish_time;
    private String series;
    private String mosaic;
    private long duration;
    private String describe;
    private long score;
    private String recommend;
    private byte[] cut1;
    private byte[] cut2;
    private byte[] cut3;
    private String reserve_field1;
    private String reserve_field2;
    private String reserve_field3;
    private boolean have;

    private List<HVAJapanAVS> sources;
    private List<HVAJapanAVPersonM> persons;
    private List<HVAJapanAVLabelM> labels;

    @Override
    public String toString() {
        if ("1".equals(recommend)) {
            return if_Code+"(推荐)";
        } else {
            return if_Code;
        }
    }
}
