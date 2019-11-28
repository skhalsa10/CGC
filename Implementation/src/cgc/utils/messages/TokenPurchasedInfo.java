package cgc.utils.messages;
import cgc.utils.Entity;
import java.util.Date;
import java.time.LocalTime;
import java.util.Calendar;

/*
Message send from the payKiosk to the KiosManager,and the resend to the TansactionLog.
*/

public class TokenPurchasedInfo implements Message {
    private double amount;
    private Date purchasedDate;

    public TokenPurchasedInfo(double amount, Date purchasedDate){
        this.amount = amount;
        this.purchasedDate = purchasedDate;
    }

    public double getAmount(){
        return amount;
    }

    public Date getPurchasedDate(){
        return purchasedDate;
    }
}