package xyz.cofe.stsl.eval.swing;

import javax.swing.Timer;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.function.Consumer;

public class SwingEvents {
    public static class WndListenerDelay extends WindowAdapter {
        public final WindowAdapter target;
        public final int ms;

        public WndListenerDelay( WindowAdapter target, int ms ){
            this.target = target;
            this.ms = ms;
        }

        private void delayOnce( int ms, Runnable cons ){
            var timerRef = new Timer[1];
            timerRef[0] = new Timer(ms, e -> {
                timerRef[0].stop();
                cons.run();
            });
            var t = timerRef[0];
            t.setInitialDelay(ms);
            t.setRepeats(false);
            t.start();
        }

        @Override
        public void windowOpened( WindowEvent e ){
            delayOnce(ms, ()->target.windowOpened(e) );
        }

        @Override
        public void windowClosing( WindowEvent e ){
            delayOnce(ms, ()->target.windowClosing(e) );
        }

        @Override
        public void windowClosed( WindowEvent e ){
            delayOnce(ms, ()->target.windowClosed(e) );
        }

        @Override
        public void windowIconified( WindowEvent e ){
            delayOnce(ms, ()->target.windowIconified(e) );
        }

        @Override
        public void windowDeiconified( WindowEvent e ){
            delayOnce(ms, ()->target.windowDeiconified(e) );
        }

        @Override
        public void windowActivated( WindowEvent e ){
            delayOnce(ms, ()->target.windowActivated(e) );
        }

        @Override
        public void windowDeactivated( WindowEvent e ){
            delayOnce(ms, ()->target.windowDeactivated(e) );
        }

        @Override
        public void windowStateChanged( WindowEvent e ){
            delayOnce(ms, ()->target.windowStateChanged(e) );
        }

        @Override
        public void windowGainedFocus( WindowEvent e ){
            delayOnce(ms, ()->target.windowGainedFocus(e) );
        }

        @Override
        public void windowLostFocus( WindowEvent e ){
            delayOnce(ms, ()->target.windowLostFocus(e) );
        }
    }

    public static class WND {
        public final Window wnd;
        public int delay;

        public WND(Window wnd){
            if( wnd==null )throw new IllegalArgumentException( "wnd==null" );
            this.wnd = wnd;
        }
        public WND(WND sample){
            if( sample==null )throw new IllegalArgumentException( "sample==null" );
            wnd = sample.wnd;
            delay = sample.delay;
        }

        public WND opened( Consumer<WindowEvent> cons){
            if( cons==null )throw new IllegalArgumentException( "cons==null" );
            var exec = new WindowAdapter() {
                @Override
                public void windowOpened( WindowEvent e ){
                    cons.accept(e);
                }
            };
            wnd.addWindowListener( delay > 0 ? new WndListenerDelay(exec,delay) : exec );
            return this;
        }

        public WND opened( Runnable cons ){
            if( cons==null )throw new IllegalArgumentException( "cons==null" );
            return opened( windowEvent -> cons.run() );
        }

        public WND delay( int ms ){
            var w = new WND(this);
            w.delay = ms;
            return w;
        }
    }

    public static WND on( Window wnd ){
        if( wnd==null )throw new IllegalArgumentException( "wnd==null" );
        return new WND(wnd);
    }
}
