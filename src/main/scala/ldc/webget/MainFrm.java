/*
 * By Luke Harvey
 * 
 */

/*
 * MainFrm.java
 *
 * Created on Dec 21, 2009, 8:03:14 AM
 */

package ldc.webget;

import net.sourceforge.jsorter.SwingSorter;
import java.awt.event.*;
import javax.swing.text.Document;
/**
 *
 * @author Luke Harvey
 */
public class MainFrm extends javax.swing.JFrame {

    /** Creates new form MainFrm */
    public MainFrm() {
        swingSorter = new SwingSorter();
        one = new OneManga();
        AllManga = one.titleSeqList();
        Log_Origins = db.getOrigins();
        //logUpdate(0,0);
        Qprog =0;
        Qmax = 0;
        Qprog_Text = "Downloading " + Qprog +" of "+Qmax +" manga";
        //AllManga = new javax.swing.DefaultListModel();
        swingSorter.sortListModel(AllManga);
        swingSorter.sortListModel(Log_Origins);
        Gets = db.getlist();
        //Gets.addElement("/C_Sword_and_Cornett/");
        
        initComponents();
       //sudo();
        //AllManga = new javax.swing.DefaultListModel();

        jTextField1.setText(db.getAHash("om_filesDir"));
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainTab = new javax.swing.JTabbedPane();
        controlPanel = new javax.swing.JPanel();
        oneMangaRun = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        close = new javax.swing.JButton();
        OM_Q_length = new javax.swing.JProgressBar();
        SelectManga = new javax.swing.JPanel();
        AllMangaList = new javax.swing.JScrollPane();
        AllMangalst = new javax.swing.JList();
        jScrollPane1 = new javax.swing.JScrollPane();
        GetMagnalst = new javax.swing.JList();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        Logs = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        ReportBox = new javax.swing.JCheckBox();
        ErrorBox = new javax.swing.JCheckBox();
        DebugBox = new javax.swing.JCheckBox();
        jScrollPane2 = new javax.swing.JScrollPane();
        Log_Origin_lst = new javax.swing.JList();
        jLabel2 = new javax.swing.JLabel();

        setTitle("WebGet");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        oneMangaRun.setText("Run OneManga");
        oneMangaRun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                oneMangaRunActionPerformed(evt);
            }
        });

        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });
        jTextField1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField1FocusLost(evt);
            }
        });

        close.setText("Close");
        close.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeActionPerformed(evt);
            }
        });

        OM_Q_length.setMaximum(Qmax);
        OM_Q_length.setOrientation(Qprog);

        javax.swing.GroupLayout controlPanelLayout = new javax.swing.GroupLayout(controlPanel);
        controlPanel.setLayout(controlPanelLayout);
        controlPanelLayout.setHorizontalGroup(
            controlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(controlPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(oneMangaRun)
                .addContainerGap(689, Short.MAX_VALUE))
            .addGroup(controlPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 398, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(398, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, controlPanelLayout.createSequentialGroup()
                .addGroup(controlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, controlPanelLayout.createSequentialGroup()
                        .addGap(77, 77, 77)
                        .addComponent(OM_Q_length, javax.swing.GroupLayout.DEFAULT_SIZE, 390, Short.MAX_VALUE))
                    .addGroup(controlPanelLayout.createSequentialGroup()
                        .addContainerGap(408, Short.MAX_VALUE)
                        .addComponent(close)))
                .addGap(339, 339, 339))
        );
        controlPanelLayout.setVerticalGroup(
            controlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(controlPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(oneMangaRun)
                .addGap(18, 18, 18)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(94, 94, 94)
                .addComponent(OM_Q_length, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 269, Short.MAX_VALUE)
                .addComponent(close)
                .addContainerGap())
        );

        mainTab.addTab("Controls", controlPanel);

        AllMangalst.setModel(AllManga);
        AllMangaList.setViewportView(AllMangalst);

        GetMagnalst.setModel(Gets);
        jScrollPane1.setViewportView(GetMagnalst);

        jButton1.setText(">");
        jButton1.setToolTipText("Add Item");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("<");
        jButton2.setToolTipText("Remove Item");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout SelectMangaLayout = new javax.swing.GroupLayout(SelectManga);
        SelectManga.setLayout(SelectMangaLayout);
        SelectMangaLayout.setHorizontalGroup(
            SelectMangaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SelectMangaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(AllMangaList, javax.swing.GroupLayout.PREFERRED_SIZE, 338, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(SelectMangaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButton1)
                    .addComponent(jButton2))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 327, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(54, 54, 54))
        );
        SelectMangaLayout.setVerticalGroup(
            SelectMangaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SelectMangaLayout.createSequentialGroup()
                .addGroup(SelectMangaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(SelectMangaLayout.createSequentialGroup()
                        .addGap(104, 104, 104)
                        .addComponent(jButton1)
                        .addGap(50, 50, 50)
                        .addComponent(jButton2))
                    .addGroup(SelectMangaLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(SelectMangaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(AllMangaList, javax.swing.GroupLayout.DEFAULT_SIZE, 461, Short.MAX_VALUE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 461, Short.MAX_VALUE))))
                .addContainerGap())
        );

        mainTab.addTab("SelectManga", SelectManga);

        jLabel1.setText("Filters:");

        ReportBox.setText("Reports");

        ErrorBox.setText("Errors");

        DebugBox.setText("Debug");

        Log_Origin_lst.setModel(Log_Origins);
        jScrollPane2.setViewportView(Log_Origin_lst);

        jLabel2.setText("Origins");

        javax.swing.GroupLayout LogsLayout = new javax.swing.GroupLayout(Logs);
        Logs.setLayout(LogsLayout);
        LogsLayout.setHorizontalGroup(
            LogsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(LogsLayout.createSequentialGroup()
                .addGroup(LogsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(LogsLayout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(ReportBox)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(ErrorBox)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(DebugBox))
                    .addGroup(LogsLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(LogsLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel2)))
                .addContainerGap(568, Short.MAX_VALUE))
        );
        LogsLayout.setVerticalGroup(
            LogsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(LogsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(LogsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(ReportBox)
                    .addComponent(ErrorBox)
                    .addComponent(DebugBox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addGap(5, 5, 5)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 421, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        mainTab.addTab("Logs", Logs);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(mainTab, javax.swing.GroupLayout.DEFAULT_SIZE, 811, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(mainTab, javax.swing.GroupLayout.DEFAULT_SIZE, 511, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        int index =  AllMangalst.getSelectedIndex();
        Object manga = AllMangalst.getSelectedValue();
        AllManga.remove(index);
        AllMangalst.setSelectedIndex(index -1);
        Gets.addElement(manga);
        swingSorter.sortListModel(Gets);
        db.setGetlist((String)manga);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        
    }//GEN-LAST:event_formWindowOpened

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        int index =  GetMagnalst.getSelectedIndex();
        Object manga = GetMagnalst.getSelectedValue();
        Gets.remove(index);
        GetMagnalst.setSelectedIndex(index -1);
        AllManga.addElement(manga);
        swingSorter.sortListModel(AllManga);
        db.dropList((String)manga);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void oneMangaRunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_oneMangaRunActionPerformed

        for (int x=0; x < Gets.size(); x++){
            one.mangaSeq((String)Gets.get(x));
        }
        DownController.setDl(jTextField1.getText());
        one.run();
    }//GEN-LAST:event_oneMangaRunActionPerformed

    private void sudo() {
        for (int x=0; x < Gets.size(); x++){
            one.mangaSeq((String)Gets.get(x));
        }
        DownController.setDl(jTextField1.getText());
        one.run();
    }

    public void logUpdate(int prog, int max){
        OM_Q_length.setMaximum(max);
        OM_Q_length.setValue(prog);
    }
    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
        
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void closeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeActionPerformed
        db.clear();
    }//GEN-LAST:event_closeActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        db.KillDB();
        one.clean();
        System.exit(0);
    }//GEN-LAST:event_formWindowClosing

    private void jTextField1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField1FocusLost
        db.updateHash("om_filesDir",jTextField1.getText());
    }//GEN-LAST:event_jTextField1FocusLost

    private SwingSorter swingSorter;
    private OneManga one;
    private javax.swing.DefaultListModel Gets;
    private javax.swing.DefaultListModel AllManga;
    private javax.swing.DefaultListModel Log_Origins;
    private int Qmax;
    private int Qprog;
    private String Qprog_Text;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane AllMangaList;
    private javax.swing.JList AllMangalst;
    private javax.swing.JCheckBox DebugBox;
    private javax.swing.JCheckBox ErrorBox;
    private javax.swing.JList GetMagnalst;
    private javax.swing.JList Log_Origin_lst;
    private javax.swing.JPanel Logs;
    private javax.swing.JProgressBar OM_Q_length;
    private javax.swing.JCheckBox ReportBox;
    private javax.swing.JPanel SelectManga;
    private javax.swing.JButton close;
    private javax.swing.JPanel controlPanel;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTabbedPane mainTab;
    private javax.swing.JButton oneMangaRun;
    // End of variables declaration//GEN-END:variables

}
