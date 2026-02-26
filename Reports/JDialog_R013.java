/*
/*
 * Modified 20 Apr 2010 (Dulyawat) แก้ชื่อโฟลเดอร์รายงานจาก "reports" เป็น "Reports" เพื่อให้ใช้งานตรงกันทั้ง Windows และ Linux.
 */
package pcu;

// * Chanthip Modified 25 Nov 2009 --> เพิ่มเงื่อนไข เลือกหน่วยงาน
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JButton;
import pcu.PersistentClass.CheckUpdate;
import pcu.PersistentClass.Office;
import pcu.PersistentClass.UserInfo;
import pcu.PersistentClass.ComboBox;
import javax.swing.DefaultComboBoxModel;

public class JDialog_R013 extends javax.swing.JDialog {
    
    private Office office;
    private String sDate,eDate,pDate,pcucode,pcu,offAddress;
    private ComboBox cb_Office;
          
    public JDialog_R013(java.awt.Dialog parent, boolean modal) {
        super(parent, modal);
//        pcucode = UserInfo.pcucode;
        initComponents();
        new CheckUpdate(getContentPane().getComponents());
        office = new Office(); 
//        offAddress = "สอ."+office.getOffname()+"("+office.getOffid()+") ต."+office.getSubdist()+" อ."+office.getDist()+" จ."+office.getProv();
        
        jXDateStart.setDate(office.getDaterepStart());
        jXDateEnd.setDate(office.getDaterepEnd());
        cb_Office = new ComboBox("SELECT CONCAT(hosname,'(',CONVERT(hoscode USING utf8),')'), offid FROM chospital, office WHERE office.offid<>'0000x' AND office.offid=chospital.hoscode ");
        cbOffice.setModel(new DefaultComboBoxModel(cb_Office.getVname()));   
        cbOffice.setSelectedIndex(cb_Office.indexOf(UserInfo.pcucode));
//        tfOffName.setText("สอ. "+office.getOffname()+"("+office.getOffid()+")");
//        tfTambon.setText(" ต."+office.getSubdist());
//        tfAmphur.setText(" อ."+office.getDist());
//        tfChangwat.setText(" จ."+office.getProv());
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jpn1 = new javax.swing.JPanel();
        lbDateStart = new javax.swing.JLabel();
        lbDateEnd = new javax.swing.JLabel();
        tfMonth = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jButtonRB1K01_4r58 = new javax.swing.JButton();
        jButtonRB1K01_4all = new javax.swing.JButton();
        jButtonRB1K01_4r55 = new javax.swing.JButton();
        jButtonRB1K01_4r56 = new javax.swing.JButton();
        jButtonRB1K01_4r57 = new javax.swing.JButton();
        jButtonRB1K01_4r54 = new javax.swing.JButton();
        jButtonRB1K01_4rRB = new javax.swing.JButton();
        jpOffice = new javax.swing.JPanel();
        cbOffice = new javax.swing.JComboBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("รบ 1 ก. 01/3 (วัคซีน)");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jpn1.setLayout(new java.awt.GridBagLayout());

        lbDateStart.setFont(new java.awt.Font("Tahoma", 0, 12));
        lbDateStart.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbDateStart.setText("ข้อมูลรายงาน ตั้งแต่วันที่:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jpn1.add(lbDateStart, gridBagConstraints);

        lbDateEnd.setFont(new java.awt.Font("Tahoma", 0, 12));
        lbDateEnd.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbDateEnd.setText("ถึงวันที่:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 29;
        gridBagConstraints.ipady = 9;
        jpn1.add(lbDateEnd, gridBagConstraints);

        tfMonth.setBackground(new java.awt.Color(236, 233, 216));
        tfMonth.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        tfMonth.setBorder(null);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        jpn1.add(tfMonth, gridBagConstraints);

        jPanel1.setLayout(new java.awt.GridBagLayout());

        jButtonRB1K01_4r58.setFont(new java.awt.Font("Tahoma", 0, 12));
        jButtonRB1K01_4r58.setText("เด็กอายุ 12 เดือน ได้รับวัคซีนหัด นับอายุถึงวันที่ระบุ");
        jButtonRB1K01_4r58.setActionCommand("vacc_meas");
        jButtonRB1K01_4r58.setName("4"); // NOI18N
        jButtonRB1K01_4r58.setPreferredSize(new java.awt.Dimension(309, 37));
        jButtonRB1K01_4r58.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRB1K01_4r58ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 8;
        jPanel1.add(jButtonRB1K01_4r58, gridBagConstraints);

        jButtonRB1K01_4all.setFont(new java.awt.Font("Tahoma", 0, 12));
        jButtonRB1K01_4all.setText("ทุกคนที่ได้รับวัคซีน");
        jButtonRB1K01_4all.setActionCommand("rrb1k013total");
        jButtonRB1K01_4all.setName(""); // NOI18N
        jButtonRB1K01_4all.setPreferredSize(new java.awt.Dimension(309, 37));
        jButtonRB1K01_4all.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRB1K01_4allActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 8;
        jPanel1.add(jButtonRB1K01_4all, gridBagConstraints);

        jButtonRB1K01_4r55.setFont(new java.awt.Font("Tahoma", 0, 12));
        jButtonRB1K01_4r55.setText("สรุปยอด การใช้วัคซีนในช่วงวันที่ระบุ");
        jButtonRB1K01_4r55.setActionCommand("rsummaryvaccine");
        jButtonRB1K01_4r55.setName(""); // NOI18N
        jButtonRB1K01_4r55.setPreferredSize(new java.awt.Dimension(309, 37));
        jButtonRB1K01_4r55.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRB1K01_4r55ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 8;
        jPanel1.add(jButtonRB1K01_4r55, gridBagConstraints);

        jButtonRB1K01_4r56.setFont(new java.awt.Font("Tahoma", 0, 12));
        jButtonRB1K01_4r56.setText("เด็กอายุต่ำกว่า 5 ปี(60 เดือน) ได้รับวัคซีน DTP5 นับอายุถึงวันที่ระบุ");
        jButtonRB1K01_4r56.setActionCommand("vacc_dtp5");
        jButtonRB1K01_4r56.setName("4"); // NOI18N
        jButtonRB1K01_4r56.setPreferredSize(new java.awt.Dimension(309, 37));
        jButtonRB1K01_4r56.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRB1K01_4r56ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 8;
        jPanel1.add(jButtonRB1K01_4r56, gridBagConstraints);

        jButtonRB1K01_4r57.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jButtonRB1K01_4r57.setText("เด็กอายุ 36 เดือน ได้รับวัคซีน JE3 นับอายุถึงวันที่ระบุ");
        jButtonRB1K01_4r57.setActionCommand("vacc_je3");
        jButtonRB1K01_4r57.setName("4"); // NOI18N
        jButtonRB1K01_4r57.setPreferredSize(new java.awt.Dimension(309, 37));
        jButtonRB1K01_4r57.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRB1K01_4r57ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 8;
        jPanel1.add(jButtonRB1K01_4r57, gridBagConstraints);

        jButtonRB1K01_4r54.setFont(new java.awt.Font("Tahoma", 0, 12));
        jButtonRB1K01_4r54.setText("เด็ก 0-5 ปี และนักเรียนที่ได้รับวัคซีน");
        jButtonRB1K01_4r54.setActionCommand("rrb1k013st");
        jButtonRB1K01_4r54.setName(""); // NOI18N
        jButtonRB1K01_4r54.setPreferredSize(new java.awt.Dimension(309, 37));
        jButtonRB1K01_4r54.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRB1K01_4r54ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 8;
        jPanel1.add(jButtonRB1K01_4r54, gridBagConstraints);

        jButtonRB1K01_4rRB.setFont(new java.awt.Font("Tahoma", 0, 12));
        jButtonRB1K01_4rRB.setText("รายชื่อผู้ได้รับวัคซีนป้องกันโรคพิษสุนัขบ้า");
        jButtonRB1K01_4rRB.setActionCommand("rptvaccrabies");
        jButtonRB1K01_4rRB.setName("4"); // NOI18N
        jButtonRB1K01_4rRB.setPreferredSize(new java.awt.Dimension(309, 37));
        jButtonRB1K01_4rRB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRB1K01_4rRBActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 8;
        jPanel1.add(jButtonRB1K01_4rRB, gridBagConstraints);

        jpOffice.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "ข้อมูลของหน่วยงาน", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 12))); // NOI18N

        cbOffice.setFont(new java.awt.Font("Tahoma", 0, 12));
        cbOffice.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbOfficeItemStateChanged(evt);
            }
        });
        cbOffice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbOfficeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jpOfficeLayout = new javax.swing.GroupLayout(jpOffice);
        jpOffice.setLayout(jpOfficeLayout);
        jpOfficeLayout.setHorizontalGroup(
            jpOfficeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpOfficeLayout.createSequentialGroup()
                .addGap(125, 125, 125)
                .addComponent(cbOffice, javax.swing.GroupLayout.PREFERRED_SIZE, 333, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(137, Short.MAX_VALUE))
        );
        jpOfficeLayout.setVerticalGroup(
            jpOfficeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpOfficeLayout.createSequentialGroup()
                .addComponent(cbOffice, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jpOffice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 611, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jpn1, javax.swing.GroupLayout.PREFERRED_SIZE, 611, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(84, 84, 84))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jpOffice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jpn1, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
                .addContainerGap())
        );

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-640)/2, (screenSize.height-433)/2, 640, 433);
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonRB1K01_4r52ActionPerformed(java.awt.event.ActionEvent evt) {                                                   
        print(evt);
    }                                                                                                    

    private void jButtonRB1K01_4r89ActionPerformed(java.awt.event.ActionEvent evt) {                                                   
        print(evt);
    }                                                                                                    

    private void jButtonRB1K01_4r02ActionPerformed(java.awt.event.ActionEvent evt) {                                                   
        print(evt);
    }                                                                                                    

    private void jButtonRB1K01_4allActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRB1K01_4allActionPerformed
        print(evt);
    }//GEN-LAST:event_jButtonRB1K01_4allActionPerformed

private void jButtonRB1K01_4r55ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRB1K01_4r55ActionPerformed

    print(evt);
}//GEN-LAST:event_jButtonRB1K01_4r55ActionPerformed

private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
    dispose();
    getOwner().setVisible(true);
}//GEN-LAST:event_formWindowClosing

private void jButtonRB1K01_4r56ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRB1K01_4r56ActionPerformed
    print(evt);
}//GEN-LAST:event_jButtonRB1K01_4r56ActionPerformed

private void jButtonRB1K01_4r57ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRB1K01_4r57ActionPerformed
    print(evt);
}//GEN-LAST:event_jButtonRB1K01_4r57ActionPerformed

private void jButtonRB1K01_4r58ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRB1K01_4r58ActionPerformed
    print(evt);
}//GEN-LAST:event_jButtonRB1K01_4r58ActionPerformed

private void cbOfficeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbOfficeItemStateChanged
    if (evt.getStateChange() == 1) {  
        if (cbOffice.getSelectedIndex() < 1) {
            pcucode = "";
        } else {
            pcu = cb_Office.getCode(cbOffice);
            pcucode = " AND v.pcucode = '" + pcu + "'";
        }    
    }
}//GEN-LAST:event_cbOfficeItemStateChanged

private void jButtonRB1K01_4rRBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRB1K01_4rRBActionPerformed
print(evt);
}//GEN-LAST:event_jButtonRB1K01_4rRBActionPerformed

private void cbOfficeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbOfficeActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_cbOfficeActionPerformed

private void jButtonRB1K01_4r53ActionPerformed(java.awt.event.ActionEvent evt) {                                                   
        print(evt);
}                                                                                                    

private void jButtonRB1K01_4r54ActionPerformed(java.awt.event.ActionEvent evt) {                                                   
        print(evt);
}                                                                                                    
    
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                UserInfo.pcucode="08727";
                new JDialog_R013(new javax.swing.JDialog(), true).setVisible(true);
            }
        });
    }
    
    private void print(java.awt.event.ActionEvent evt){
        if (cbOffice.getSelectedIndex() < 1) {
            pcucode = "";
        } else {
            pcu = cb_Office.getCode(cbOffice);
            pcucode = " AND v.pcucode = '" + pcu + "'";
        } 
        sDate = jXDateStart.getDate();        
        eDate = jXDateEnd.getDate();
        pDate = new java.sql.Date(new Date().getTime()).toString();
        Map pm = new HashMap();
        pm.put("sdate",sDate);
        pm.put("edate",eDate);
        if(((JButton)evt.getSource()).getName().equals("4"))
         pm.put("pdate",eDate);
        else
        pm.put("pdate",eDate);
        pm.put("pcucode",pcucode);
        pm.put("SUBREPORT_DIR", "./Reports/");
        setVisible(false);
        new RunReport(this, pm,evt.getActionCommand()+".jasper");
    }
  
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cbOffice;
    private javax.swing.JButton jButtonRB1K01_4all;
    private javax.swing.JButton jButtonRB1K01_4r54;
    private javax.swing.JButton jButtonRB1K01_4r55;
    private javax.swing.JButton jButtonRB1K01_4r56;
    private javax.swing.JButton jButtonRB1K01_4r57;
    private javax.swing.JButton jButtonRB1K01_4r58;
    private javax.swing.JButton jButtonRB1K01_4rRB;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jpOffice;
    private javax.swing.JPanel jpn1;
    private javax.swing.JLabel lbDateEnd;
    private javax.swing.JLabel lbDateStart;
    private javax.swing.JTextField tfMonth;
    // End of variables declaration//GEN-END:variables
    
}
