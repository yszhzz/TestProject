package cn.mypro.utils;

public class RandomDataGenerateUtil {

    private IRandomDataGenerate instance = IRandomDataGenerate.INSTANCE;

    public void init(int seed) {
        instance.initRandData(seed);
    }

    private int getRandData() {
        return instance.getRandData();
    }

    public String getFactor() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            String randomString = Integer.toHexString(getRandData());
            for (int j = randomString.length(); j < 8; j++) {
                sb.append('0');
            }
            sb.append(randomString);
        }
        return sb.toString();
    }
}
