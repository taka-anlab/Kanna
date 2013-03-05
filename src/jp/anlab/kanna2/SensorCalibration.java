package jp.anlab.kanna2;
/*
 * センサーの値をキャリブレーションして返すクラス
 */
public class SensorCalibration {
    float btnSensorValue;
    float nowSensorValue;

    void setBtnSensorValue(float i_btn) {
        btnSensorValue = i_btn;
    }

    float setNowSensorValue(float i_now) {
        float y_kosei = i_now * 10;
        y_kosei = Math.round(y_kosei);
        y_kosei = y_kosei / 10;
        y_kosei = ((y_kosei) * 10 - (btnSensorValue) * 10) / 10;
        return y_kosei;
    }
}