import javax.swing.*;

/**
 * Created by Lada on 22.2.14.
 */
public class CarSelector extends JComboBox {

   public void setItems(CarArrayList carnewlist, int position){
       if (carnewlist.getCount()==0) return;
       if (position==-1) position=0;
       this.removeAllItems();
       for (int i = 0; i < carnewlist.getCount(); i++) {
           CarArrayList.CarInfo selectedCar = (CarArrayList.CarInfo) carnewlist.cars.get(i);
           this.addItem("Car " + selectedCar.id);
       }
       this.setSelectedIndex(position);
   }

}
