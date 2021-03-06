package com.ali.address911Utils;

import android.os.Handler;

import java.util.ArrayList;

public class AddressRegistrantList {
    ArrayList registrants = new ArrayList();      // of Registrant
    public synchronized void add(Handler h, int what, Object obj) {
        add(new AddressRegistrant(h, what, obj));
    }

    public synchronized void addUnique(Handler h, int what, Object obj) {
        // if the handler is already in the registrant list, remove it
        remove(h);
        add(new AddressRegistrant(h, what, obj));
    }

    public synchronized void add(AddressRegistrant r) {
        removeCleared();
        registrants.add(r);
    }

    public synchronized void removeCleared() {
        for (int i = registrants.size() - 1; i >= 0 ; i--) {
            AddressRegistrant  r = (AddressRegistrant) registrants.get(i);

            if (r.refH == null) {
                registrants.remove(i);
            }
        }
    }

    public synchronized int size() {
        return registrants.size();
    }

    public synchronized Object get(int index) {
        return registrants.get(index);
    }

    private synchronized void internalNotifyRegistrants (Object result, Throwable exception) {
        for (int i = 0, s = registrants.size(); i < s ; i++) {
            AddressRegistrant  r = (AddressRegistrant) registrants.get(i);
            r.internalNotifyRegistrant(result, exception);
        }
    }

    public /*synchronized*/ void notifyRegistrants() {
        internalNotifyRegistrants(null, null);
    }

    public void notifyCancel(){

    }
    public /*synchronized*/ void notifyException(Throwable exception) {
        internalNotifyRegistrants (null, exception);
    }

    public /*synchronized*/ void notifyResult(Object result) {
        internalNotifyRegistrants (result, null);
    }


//    public /*synchronized*/ void
//    notifyRegistrants(AsyncResult ar) {
//        internalNotifyRegistrants(ar.result, ar.exception);
//    }

    public synchronized void remove(Handler h) {
        for (int i = 0, s = registrants.size() ; i < s ; i++) {
            AddressRegistrant  r = (AddressRegistrant) registrants.get(i);
            Handler rh;

            rh = r.getHandler();
//            int mt = r.messageForRegistrant().what;
            /* Clean up both the requested registrant and
             * any now-collected registrants
             */
            if (rh == null || rh == h) {
                r.clear();
            }
        }

        removeCleared();
    }
}
