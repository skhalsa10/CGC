package cgc.utils.messages;
import java.util.ArrayList;

/**
 * This message is sent by the Kiosk Manager to the CGC and contains all the information related
 * with the financial depart ment.
 */
public class UpdatedFinanceInfo implements Message {
    private double totalBenefits;
    private ArrayList <Double> monthBenefits;
    private ArrayList <Integer> typeTicketsSold;

    public UpdatedFinanceInfo(double totalBenefits, ArrayList <Double> monthBenefits, ArrayList <Integer> typeTicketsSold ) {
        this.totalBenefits = totalBenefits;
        this.monthBenefits = monthBenefits;
        this.typeTicketsSold = typeTicketsSold;
    }
    
    //Return the beneficts from that month.
    public Double getMensualBenefits(int month){

        return(monthBenefits.get(month));
    }

    //Return the beneficts of every month
    public ArrayList<Double> getMonthsBenefits(){
        
        return(this.monthBenefits);
    }

    //Return the totalBeneficts of the park.
    public Double getTotalBenefits(){

        return(totalBenefits);
    }

    //Return the type of tickets that have been sold
    public ArrayList<Integer> getTypeTicketsSold(){

        return(this.typeTicketsSold);
    }

}
