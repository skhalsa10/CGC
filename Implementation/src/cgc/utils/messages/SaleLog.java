package cgc.utils.messages;

import cgc.kioskmanager.TicketPrice;

import java.util.Date;

public class SaleLog implements Message{
    private double amount;
    private Date purchasedDate;
    private TicketPrice ticketType;

    public SaleLog(double amount, Date purchasedDate, TicketPrice ticketType) {
        this.amount = amount;
        this.purchasedDate = purchasedDate;
        this.ticketType = ticketType;
    }

    public double getAmount() {
        return amount;
    }

    public Date getPurchasedDate() {
        return purchasedDate;
    }

    public TicketPrice getTicketType() {
        return ticketType;
    }
}
