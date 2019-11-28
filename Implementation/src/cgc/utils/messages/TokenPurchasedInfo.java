package cgc.utils.messages;
import cgc.kioskmanager.TicketPrice;
import cgc.utils.Entity;
import javafx.geometry.Point2D;

import java.util.Date;
import java.time.LocalTime;
import java.util.Calendar;

/*
Message send from the payKiosk to the KiosManager,and the resend to the TansactionLog.
*/

public class TokenPurchasedInfo implements Message {
    private double amount;
    private Date purchasedDate;
    private Point2D location;
    private TicketPrice typeTicket;

    public TokenPurchasedInfo(double amount, Date purchasedDate, Point2D location, TicketPrice typeTicket){
        this.amount = amount;
        this.purchasedDate = purchasedDate;
        this.location = location;
        this.typeTicket = typeTicket;
    }

    public double getAmount(){
        return amount;
    }

    public Date getPurchasedDate(){
        return purchasedDate;
    }

    public Point2D getLocation() {
        return location;
    }

    public cgc.kioskmanager.TicketPrice getTypeTicket() {
        return typeTicket;
    }
}