package org.firstinspires.ftc.teamcode;

public class EncoderValueList {

    private int LB = 0;
    private int LF = 0;
    private int RB = 0;
    private int RF = 0;

    public EncoderValueList(int LB, int LF, int RB, int RF) {
        this.LB = LB;
        this.LF = LF;
        this.RB = RB;
        this.RF = RF;
    }


    public int getLB() {
        return LB;
    }

    public int getLF() {
        return LF;
    }

    public int getRB() {
        return RB;
    }

    public int getRF() {
        return RF;
    }
}
