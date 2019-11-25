package cgc.utils.messages;
import java.util.ArrayList;

/**
 * This message is sent by the Kiosk Manager to the CGC and contains all the information related
 * with the financial depart ment.
 */
public class UpdatedFinanceInfo implements Message {
    double totalBenefits;
    ArrayList <Double> monthBenefits;

    public UpdatedFinanceInfo(double totalBenefits, ArrayList <Double> monthBenefits) {
        this.totalBenefits = totalBenefits;
        this.monthBenefits = monthBenefits;
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
}
