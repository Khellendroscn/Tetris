package net.khe.tetris;

import javax.swing.*;
import java.awt.*;

/**
 * Created by hyc on 2016/11/11.
 */
public class InputAddrDialog extends JDialog{
    private String host;
    private int pot;
    private JTextField hostField = new JTextField(15);
    private JTextField potField = new JTextField(4);
    public JButton acceptButton = new JButton("ȷ��");
    public InputAddrDialog(JFrame parent){
        super(parent);
        setTitle("�������������ַ�Ͷ˿ں�");
        potField.setText("7926");
        add(BorderLayout.CENTER,new JPanel(){
            {
                setLayout(new FlowLayout());
                add(new JLabel("������"));
                add(hostField);
                add(new JLabel("�˿ڣ�"));
                add(potField);
            }
        });
        add(BorderLayout.SOUTH,acceptButton);
        acceptButton.addActionListener((ActionEvent)->{
                dispose();
        });
        setSize(250,150);
        setVisible(true);
    }
    public String getHost(){
        return hostField.getText();
    }
    public int getPot(){
        return new Integer(potField.getText());
    }
    public void setHost(String host){
        hostField.setText(host);
    }
    public void setPot(int pot){
        potField.setText(""+pot);
    }
    public void setEditable(boolean editable){
        hostField.setEditable(editable);
        potField.setEditable(editable);
    }
    public static void main(String[] args) {
        new InputAddrDialog(null);
    }
}
