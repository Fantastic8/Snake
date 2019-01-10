package tools;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public abstract class AddButton {
	private ImageIcon imageicon;
	private JLabel label;
	private JButton button;
	public abstract void event();
	public AddButton(JPanel panel,int x,int y,String imageurl)
	{
		imageicon=new ImageIcon(AddButton.class.getResource(imageurl));//add picture
		label=new JLabel(imageicon);//picture container-label
		label.setBounds(x, y, imageicon.getIconWidth(), imageicon.getIconHeight());
		panel.add(label);
		button=new JButton();//add button
		button.setContentAreaFilled(false);
		button.setBorderPainted(false);
		button.setBounds(x, y, imageicon.getIconWidth(), imageicon.getIconHeight());
		panel.add(button);
		//set click event
		button.addMouseListener(new MouseAdapter() {
			  @Override
			  public void mousePressed(MouseEvent e) {
				  imageicon.setImage(imageicon.getImage().getScaledInstance(imageicon.getIconWidth()-2,imageicon.getIconHeight()-3 , Image.SCALE_DEFAULT));//90% scale
				  label.setIcon(imageicon);
			  }
			  @Override
			  public void mouseReleased(MouseEvent e)
			  {
				  imageicon.setImage(imageicon.getImage().getScaledInstance(imageicon.getIconWidth()+2,imageicon.getIconHeight()+3 , Image.SCALE_DEFAULT));//90% scale
				  label.setIcon(imageicon);
				  event();
			  }
			});
	}
	public ImageIcon getImageicon() {
		return imageicon;
	}
	public JLabel getLabel() {
		return label;
	}
	public JButton getButton() {
		return button;
	}
}
