package arrayexplorer.control;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;

import arrayexplorer.model.IterableExpression;

public class LineChartController extends AbstractChartController {

	
	public LineChartController(Composite parent, IterableExpression exp, Object field) {
		super(parent, exp, field);
	}
	
	int pointRad = 4;

	protected float pixelPerValue = 20;

	
	@Override
	protected void draw(GC g, int from, int to) {
		int currX = from * itemWidth + barStartMargin[0];
		int originY =  view.getCanvas().getSize().y - barStartMargin[1];
		int maxY = minVal < maxVal ? originY - barEndMargin[1] : 0;
		g.drawLine(barStartMargin[0], originY, getRequiredWidth(), originY);
		g.drawLine(barStartMargin[0] - 3, originY - maxY, barStartMargin[0], originY - maxY);

		String yAxisMaxCaption = formatter.format(maxVal);
		int maxFreqTextX = barStartMargin[0] - 5
				- g.getFontMetrics().getAverageCharWidth()
				* yAxisMaxCaption.length();
		g.drawLine(barStartMargin[0], originY + 1, barStartMargin[0], originY - maxY - 5);
		g.drawString(yAxisMaxCaption, maxFreqTextX, originY - maxY - g.getFontMetrics().getHeight() / 2);
		
		if (maxVal != minVal) {
			String yAxisMinCaption = formatter.format(minVal);
			int minFreqTextX = barStartMargin[0] - 5
					- g.getFontMetrics().getAverageCharWidth()
					* yAxisMinCaption.length();
			g.drawLine(barStartMargin[0], originY + 1, barStartMargin[0], originY - maxY - 5);
			g.drawString(yAxisMinCaption, minFreqTextX, originY - g.getFontMetrics().getHeight() / 2);
		}
		int prevX = -1, prevY = -1;
		for (int ind = from; ind <= to; ind++) {
			int currY =  originY - (int)(getArrayElement(ind).doubleValue() * maxY / maxVal);
			if (ind != from) {
				g.drawLine(currX, currY, prevX, prevY);
			}
			g.setBackground(itemColor);
			g.fillArc(currX - pointRad, currY - pointRad, 2 * pointRad, 2 * pointRad, 0, 360);
			if (ind == from || ind == to) {
				String str = String.valueOf(ind);
				g.setBackground(WHITE);
				g.drawString(str, currX + itemWidth / 3, originY + 5);
			}
			prevX = currX;
			prevY = currY;
			currX += itemWidth;
		}
	}

	@Override
	protected int getItemCount() {
		return getArraySize();
	}


	@Override
	protected int getItemIndAt(int x, int y) {
		return 0;
	}

}
