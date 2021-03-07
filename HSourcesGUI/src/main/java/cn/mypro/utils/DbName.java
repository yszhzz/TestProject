package cn.mypro.utils;

public enum DbName {
    HW("hw"), HW1("hw1"), HW2("hw2"), HW3("hw3"), HW4("hw4"), IMMIG("immig"), IMMIG1("immig1"), IMMIG2("immig2"), IMMIG3("immig3"), IMMIG4("immig4"), CORE("core"), JZZ("jzz"), IMMIGTWN("immigtwn"), IMMIGNEW("immignew"), FIIS("fiis"), PASS("pass"), LOCAL("local"), HYZX("hyzx");
    private String name;

    DbName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
