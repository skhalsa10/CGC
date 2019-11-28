package cgc.kioskmanager;
import java.util.ArrayList;
import java.util.Date;
import java.time.LocalTime;
import java.util.Calendar;

/**
 * The purpose of this class is to help analyze the finances generated
 * from all Pay Kiosks that are are generating data. This class is controlled by the
 * Kiosk Manager. this will not need to run in it's own thread. the Kiosk manager
 * can control all aspects  of this class. This class will be left up to the maker of the Kiosk Manager
 * to build out. ALL finance related action should be done using this class.
 */
public class TransactionLogger {

    //Variables related with finances.
    private Double totalBenefits;
    private ArrayList<Double> mensualBenefits;
    
    public TransactionLogger() {
        initializeMensualBenefits();
        totalBenefits = 0.0;
    }

    //Register the sale of a ticket.
    public void registerSale(double amount, Date purcharseDate){
        totalBenefits+= amount;

        //Mensual approach
        Calendar cal = Calendar.getInstance();
        cal.setTime(purcharseDate);
        int month = cal.get(Calendar.MONTH);
        double month_amount = mensualBenefits.get(month) + amount;

        //Setting the amount in the corresponding month
        mensualBenefits.set(month, month_amount);
    }


    //Initialize the Mensual Benefits Array to 0.
    private void initializeMensualBenefits(){
        mensualBenefits = new ArrayList<>(12);

        //Set the benefits of each month to 0.
        for(int i = 0; i < 12; i++){
            mensualBenefits.add(i, 0.0);
        }
    }

    //Return the beneficts from that month.
    public Double getMensualBenefits(int month){

        return(mensualBenefits.get(month));
    }

    //Return the beneficts of every month
    public ArrayList<Double> getMonthsBenefits(){
        
        return(this.mensualBenefits);
    }

    //Return the totalBeneficts of the park.
    public Double getTotalBenefits(){

        return(totalBenefits);
    }
}
