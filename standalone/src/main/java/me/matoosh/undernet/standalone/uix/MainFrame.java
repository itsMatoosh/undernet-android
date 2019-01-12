package me.matoosh.undernet.standalone.uix;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import me.matoosh.undernet.UnderNet;
import me.matoosh.undernet.event.Event;
import me.matoosh.undernet.event.EventHandler;
import me.matoosh.undernet.event.EventManager;
import me.matoosh.undernet.event.resource.transfer.ResourceTransferDataReceivedEvent;
import me.matoosh.undernet.event.resource.transfer.ResourceTransferDataSentEvent;
import me.matoosh.undernet.event.resource.transfer.ResourceTransferFinishedEvent;
import me.matoosh.undernet.event.router.RouterStatusEvent;
import me.matoosh.undernet.p2p.router.InterfaceStatus;
import me.matoosh.undernet.p2p.router.data.resource.transfer.FileTransferHandler;
import me.matoosh.undernet.p2p.router.data.resource.transfer.ResourceTransferHandler;
import me.matoosh.undernet.standalone.UnderNetStandalone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.util.ResourceBundle;

public class MainFrame extends EventHandler {

    /**
     * The logger of the class.
     */
    public static Logger logger = LoggerFactory.getLogger(MainFrame.class);

    /**
     * The instance of the class.
     */
    public static MainFrame instance;

    /**
     * The frame.
     */
    public JFrame frame;

    private JPanel panel;
    private ResourcePanel resourcePanel;
    private NodePanel nodePanel;
    private TunnelPanel tunnelPanel;
    private JProgressBar progressBar;
    private JButton mainButton;
    private VisualPanel visualPanel;
    private ControlIcon controlIcon1;

    public static final int START_HEIGHT = 600;
    public static final int START_WIDTH = 950;

    public MainFrame() {
        $$$setupUI$$$();
        mainButton.addActionListener(e -> onMainButtonClicked());
    }

    public static void newInstance() {
        if (instance != null) {
            logger.warn("Can't open more than one Mainframe!");
            return;
        }
        logger.info("Opening the Mainframe...");
        instance = new MainFrame();

        instance.frame = new JFrame("UnderNet");
        instance.frame.setContentPane(instance.panel);
        instance.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        instance.initialize();
        instance.frame.pack();
        instance.frame.setSize(START_WIDTH, START_HEIGHT);

        instance.frame.setVisible(true);
    }

    private void initialize() {
        registerListener();
    }

    private void registerListener() {
        EventManager.registerHandler(this, RouterStatusEvent.class);
        EventManager.registerHandler(this, ResourceTransferDataReceivedEvent.class);
        EventManager.registerHandler(this, ResourceTransferDataSentEvent.class);
    }

    /**
     * Called when the main button is clicked.
     */
    public void onMainButtonClicked() {
        if (UnderNet.router.status == InterfaceStatus.STOPPED)
            UnderNetStandalone.connect();
        else if (UnderNet.router.status == InterfaceStatus.STARTED)
            UnderNetStandalone.disconnect();
    }

    @Override
    public void onEventCalled(Event e) {
        //router status event
        if (e instanceof RouterStatusEvent) {
            RouterStatusEvent statusEvent = (RouterStatusEvent) e;

            switch (statusEvent.newStatus) {
                case STOPPED:
                    mainButton.setEnabled(true);
                    mainButton.setText(ResourceBundle.getBundle("language").getString("button_connect"));
                    this.frame.repaint();
                    break;
                case STARTED:
                    instance.mainButton.setEnabled(true);
                    instance.mainButton.setText(ResourceBundle.getBundle("language").getString("button_disconnect"));
                    break;
                case STOPPING:
                    mainButton.setEnabled(false);
                    break;
                case STARTING:
                    mainButton.setEnabled(false);
                    new Thread(() -> drawLoop()).start();
                    break;
            }
        } else if (e instanceof ResourceTransferDataReceivedEvent) {
            ResourceTransferDataReceivedEvent dataReceivedEvent = (ResourceTransferDataReceivedEvent) e;
            ResourceTransferHandler transferHandler = dataReceivedEvent.transferHandler;

            if (transferHandler instanceof FileTransferHandler) {
                FileTransferHandler fileTransferHandler = (FileTransferHandler) transferHandler;

                progressBar.setValue((int) (((float) fileTransferHandler.getWritten()) / ((float) fileTransferHandler.getFileLength()) * 100f));
            }
        } else if (e instanceof ResourceTransferDataSentEvent) {
            ResourceTransferDataSentEvent dataReceivedEvent = (ResourceTransferDataSentEvent) e;
            ResourceTransferHandler transferHandler = dataReceivedEvent.transferHandler;

            if (transferHandler instanceof FileTransferHandler) {
                FileTransferHandler fileTransferHandler = (FileTransferHandler) transferHandler;

                progressBar.setValue((int) (((float) fileTransferHandler.getSent()) / ((float) fileTransferHandler.getFileLength()) * 100f));
            }
        } else if (e instanceof ResourceTransferFinishedEvent) {
            ResourceTransferFinishedEvent transferFinishedEvent = (ResourceTransferFinishedEvent) e;
            ResourceTransferHandler transferHandler = transferFinishedEvent.transferHandler;

            if (transferHandler instanceof FileTransferHandler) {
                progressBar.setValue(0);
            }
        }
    }

    /**
     * The draw loop logic.
     */
    private void drawLoop() {
        int FRAMES_PER_SECOND = 30;
        int SKIP_TICKS = 1000 / FRAMES_PER_SECOND;

        long next_game_tick = System.currentTimeMillis();
        long sleep_time = 0;

        while (UnderNet.router != null && UnderNet.router.status != InterfaceStatus.STOPPED) {
            next_game_tick += SKIP_TICKS;
            sleep_time = next_game_tick - System.currentTimeMillis();
            if (sleep_time >= 0) {
                try {
                    visualPanel.getPanel().repaint();
                    Thread.sleep(sleep_time);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                logger.warn("Can't keep up! Did the system time change, or is the node overloaded?");
            }
        }
    }

    private void createUIComponents() {
        controlIcon1 = new ControlIcon();
        mainButton = new JButton(ResourceBundle.getBundle("language").getString("button_connect"));
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        panel = new JPanel();
        panel.setLayout(new GridLayoutManager(6, 5, new Insets(0, 0, 0, 0), -1, -1));
        final Spacer spacer1 = new Spacer();
        panel.add(spacer1, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        resourcePanel = new ResourcePanel();
        panel.add(resourcePanel.$$$getRootComponent$$$(), new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(200, -1), null, null, 0, false));
        nodePanel = new NodePanel();
        panel.add(nodePanel.$$$getRootComponent$$$(), new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(300, -1), null, null, 0, false));
        tunnelPanel = new TunnelPanel();
        panel.add(tunnelPanel.$$$getRootComponent$$$(), new GridConstraints(2, 0, 1, 5, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel.add(mainButton, new GridConstraints(4, 0, 1, 5, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 50), null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel.add(spacer2, new GridConstraints(3, 0, 1, 5, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        panel.add(spacer3, new GridConstraints(1, 0, 1, 5, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        progressBar = new JProgressBar();
        panel.add(progressBar, new GridConstraints(5, 0, 1, 5, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer4 = new Spacer();
        panel.add(spacer4, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        visualPanel = new VisualPanel();
        panel.add(visualPanel.$$$getRootComponent$$$(), new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(350, -1), null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel;
    }
}
