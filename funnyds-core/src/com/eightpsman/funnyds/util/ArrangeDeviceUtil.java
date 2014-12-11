package com.eightpsman.funnyds.util;

import com.eightpsman.funnyds.core.Bound;
import com.eightpsman.funnyds.core.Device;

import java.util.List;

/**
 * FunnyDS
 * Created by 8psman on 11/25/2014.
 * Email: 8psman@gmail.com
 */
public class ArrangeDeviceUtil {


    public static class HorizontalArranger implements DeviceArranger{
        @Override
        public void arrange(List<Device> devices) {
            if (devices == null) return;
            if (devices.size() == 0) return;
            float width = 0; for (Device dv : devices)  width += dv.pw; // sum of width of all devices
            // set position for the first device
            Device dv0 = devices.get(0);
            dv0.py = 0;
            dv0.px = -width/2 + dv0.pw/2;
            // set position for the rest of devices, next to the previous device
            for (int i=1; i<devices.size(); i++){
                Device device = devices.get(i);
                Device leftDevice = devices.get(i-1);
                device.px = leftDevice.px + leftDevice.pw/2 + device.pw/2;
                device.py = 0;
            }
        }
    }

    public static class VerticalArranger implements DeviceArranger{
        @Override
        public void arrange(List<Device> devices) {
            if (devices == null) return;
            if (devices.size() == 0) return;
            float height = 0; for (Device dv : devices) height += dv.ph; // sum of width of all devices
            // set position for the first device
            Device dv0 = devices.get(0);
            dv0.px = 0;
            dv0.py = -height/2 + dv0.ph/2;
            // set position for the rest of devices, below previous device
            for (int i=1; i<devices.size(); i++){
                Device device = devices.get(i);
                Device upDevice = devices.get(i-1);
                device.px = 0;
                device.py = upDevice.py + upDevice.ph/2 + device.ph/2;
            }
        }
    }

    public static class HorizontalBoxArrranger implements DeviceArranger{
        @Override
        public void arrange(List<Device> devices) {
            if (devices == null)
                return;
            if (devices.size() == 0)
                return;

            int total = devices.size();
            int center = (int)Math.sqrt(total);
            int i = center, j=center;
            while (i * j != total){
                j++;
                if (j>total){
                    i--;
                    j = center;
                }
            }
            int row, col;
            if (isHor()){ // horizontal
                if (i > j){
                    col = i; row = j;
                }else{
                    col = j; row = i;
                }
            }else{ // vertical
                if (i < j){
                    col = i; row = j;
                }else{
                    col = j; row = i;
                }
            }

            // first row
            for (int c=1; c<col; c++)
                putDeviceRight(devices.get(c-1), devices.get(c));

            // the rest of rows
            int r = 1;
            int c = 0;
            int index;
            while ((index = r * col + c) < total){
                int upIndex = (r-1) * col + c;
                putDeviceBelow(devices.get(upIndex), devices.get(index));
                c++;
                if (c == col){
                    r++;
                    c = 0;
                }
            }

            /** move all devices to center */
            Bound bound = getBound(devices);
            float delx = bound.centerX();
            float dely = bound.centerY();
            for (int d=0; d<devices.size(); d++){
                devices.get(d).px -= delx;
                devices.get(d).py -= dely;
            }
        }

        protected boolean isHor(){
            return true;
        }

        public static class VerticalBoxArrranger extends HorizontalBoxArrranger{
            protected boolean isHor(){
                return false;
            }
        }
    }

    /** Get bound for a list of devices */
    public static Bound getBound(List<Device> devices){
        Bound bound = new Bound();
        if (devices.size() == 0) return bound;
        Device dv0 = devices.get(0);
        bound.left 		= dv0.px - dv0.pw/2;
        bound.right 	= dv0.px + dv0.pw/2;
        bound.top 	 	= dv0.py - dv0.ph/2;
        bound.bottom 	= dv0.py + dv0.ph/2;
        for (Device device : devices){
            float dvleft    = device.px - device.pw/2;
            float dvRight   = device.px + device.pw/2;
            float dvTop     = device.py - device.ph/2;
            float dvBottom  = device.py + device.ph/2;

            if (dvleft 	< bound.left) 	bound.left 		= dvleft;
            if (dvTop   < bound.top) 	bound.top 		= dvTop;
            if (dvRight	> bound.right) 	bound.right 	= dvRight;
            if (dvBottom> bound.bottom) bound.bottom 	= dvBottom;
        }
        return bound;
    }

    /**
     * put device @right next to the right of @left device
     * @param left
     * @param right
     */
    public static void putDeviceRight(Device left, Device right){
        right.py = left.py;
        right.px = left.px + left.pw/2 + right.pw/2;
    }

    /**
     * put device @below right below the @above device
     * @param above
     * @param below
     */
    public static void putDeviceBelow(Device above, Device below){
        below.px = above.px;
        below.py = above.py + above.ph/2 + below.ph/2;
    }

    public interface DeviceArranger{
        void arrange(List<Device> devices);
    }
}
