/*
 * TextClassificationForm.java
 *
 * Created on __DATE__, __TIME__
 */

package edu.fudan.example.nlp;

/**
 *
 * @author  __USER__
 */
public class TextClassificationForm extends javax.swing.JFrame {

	/** Creates new form TextClassificationForm */
	public TextClassificationForm() {
		initComponents();
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	//GEN-BEGIN:initComponents
	// <editor-fold defaultstate="collapsed" desc="Generated Code">
	private void initComponents() {

		jScrollPane1 = new javax.swing.JScrollPane();
		jTextArea1 = new javax.swing.JTextArea();
		jScrollPane2 = new javax.swing.JScrollPane();
		jTextPane1 = new javax.swing.JTextPane();
		jButton1 = new javax.swing.JButton();

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

		jTextArea1.setColumns(20);
		jTextArea1.setRows(5);
		jScrollPane1.setViewportView(jTextArea1);

		jScrollPane2.setViewportView(jTextPane1);

		jButton1.setText("\u9884\u6d4b");
		jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				jButton1MouseClicked(evt);
			}
		});

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(
				getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addGap(44, 44, 44)
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.LEADING)
												.addGroup(
														layout.createSequentialGroup()
																.addComponent(
																		jButton1)
																.addGap(43, 43,
																		43)
																.addComponent(
																		jScrollPane2,
																		javax.swing.GroupLayout.PREFERRED_SIZE,
																		173,
																		javax.swing.GroupLayout.PREFERRED_SIZE))
												.addComponent(
														jScrollPane1,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														402,
														javax.swing.GroupLayout.PREFERRED_SIZE))
								.addContainerGap(62, Short.MAX_VALUE)));
		layout.setVerticalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addGap(71, 71, 71)
								.addComponent(jScrollPane1,
										javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addGap(43, 43, 43)
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.TRAILING)
												.addComponent(jButton1)
												.addComponent(
														jScrollPane2,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE))
								.addContainerGap(100, Short.MAX_VALUE)));

		pack();
	}// </editor-fold>
	//GEN-END:initComponents

	private void jButton1MouseClicked(java.awt.event.MouseEvent evt) {
		// TODO add your handling code here:
		String str=jTextArea1.getText();
		String lable=TextClassificationInstance.getInstance().train(str);
		jTextPane1.setText(lable);
	}

	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new TextClassificationForm().setVisible(true);
			}
		});
	}

	//GEN-BEGIN:variables
	// Variables declaration - do not modify
	private javax.swing.JButton jButton1;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JScrollPane jScrollPane2;
	private javax.swing.JTextArea jTextArea1;
	private javax.swing.JTextPane jTextPane1;
	// End of variables declaration//GEN-END:variables

}