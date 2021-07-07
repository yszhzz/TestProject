package cn.mypro.swing.entity;

import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.lf5.viewer.LogTable;

import java.util.Arrays;
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
    private String robot = "0";

    private List<HVAJapanAVS> sources;
    private List<HVAJapanAVPersonM> persons;
    private List<HVAJapanAVLabelM> labels;

    private List<String> notExistsPerson;

    @Override
    public String toString() {
        if ("1".equals(recommend)) {
            return if_Code+"(推荐)";
        } else {
            return if_Code;
        }
    }

    public String getAllMessageString() {
        return "HVAJapanAVM{" +
                "uuid='" + uuid + '\'' +
                ", if_Code='" + if_Code + '\'' +
                ", oName='" + oName + '\'' +
                ", cName='" + cName + '\'' +
                ", cover=" + Arrays.toString(cover) +
                ", languages='" + languages + '\'' +
                ", production_company='" + production_company + '\'' +
                ", publish_company='" + publish_company + '\'' +
                ", publish_time='" + publish_time + '\'' +
                ", series='" + series + '\'' +
                ", mosaic='" + mosaic + '\'' +
                ", duration=" + duration +
                ", describe='" + describe + '\'' +
                ", score=" + score +
                ", recommend='" + recommend + '\'' +
                ", cut1=" + Arrays.toString(cut1) +
                ", cut2=" + Arrays.toString(cut2) +
                ", cut3=" + Arrays.toString(cut3) +
                ", reserve_field1='" + reserve_field1 + '\'' +
                ", reserve_field2='" + reserve_field2 + '\'' +
                ", reserve_field3='" + reserve_field3 + '\'' +
                ", have=" + have +
                ", sources=" + sources +
                ", persons=" + persons +
                ", labels=" + labels +
                '}';
    }
}
