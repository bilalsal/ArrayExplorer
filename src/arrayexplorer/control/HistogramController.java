package arrayexplorer.control;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;

import arrayexplorer.model.IterableExpression;

public class HistogramController extends AbstractChartController {

	int minBarSpacing = 1;

	
	int barCount;

	int maxFreq;
	
	int[] freqs;


	public HistogramController(Composite parent, IterableExpression exp,
			Object field) {
		super(parent, exp, field);
		barCount = Math.min(getArraySize() / 5, 30);
		freqs = new int[barCount];
		updateBars();
	}
	

	private void updateBars() {
		double valRange = maxVal - minVal;
		maxFreq = 0;
		for(int i = 0; i < getArraySize(); i++) {
			double val = getArrayElement(i).doubleValue();
			int barInd = (int)((val - minVal) * barCount / valRange);
			barInd = Math.min(barInd, barCount - 1);
			int freq = ++freqs[barInd];
			if (freq > maxFreq) {
				maxFreq = freq;
			}
		}
	}

	@Override
	protected void draw(GC g, int from, int to) {

		int currX = from * itemWidth + barStartMargin[0];
		int barStart =  view.getCanvas().getSize().y - barStartMargin[1];
		int maxBarLength = barStart - barEndMargin[1];
		g.drawLine(barStartMargin[0], barStart, getRequiredWidth(), barStart);
		int maxFreqY = barStart - maxBarLength;
		String maxFreqText = String.valueOf(maxFreq);
		int maxFreqTextX = barStartMargin[0] - 5 - g.getFontMetrics().getAverageCharWidth() * maxFreqText.length() ;
		g.drawLine(barStartMargin[0], barStart + 1, barStartMargin[0], maxFreqY - 10);
		g.drawLine(barStartMargin[0] - 3, maxFreqY, barStartMargin[0], maxFreqY);
		g.drawString(maxFreqText, maxFreqTextX, maxFreqY - g.getFontMetrics().getHeight() / 2);
		for (int ind = from; ind <= to; ind++) {
			int barLength = Math.max((int) (maxBarLength * freqs[ind] / maxFreq), 1);
			int startX = currX;
			int startY = barStart - barLength;
			g.setBackground(itemColor);
			g.fillRectangle(startX, startY, itemWidth, barLength);
			g.drawRectangle(startX, startY, itemWidth, barLength);
			if (ind == from || ind == to) {
				String str = formatter.format(ind == from ? minVal : maxVal);
				g.setBackground(WHITE);
				g.drawString(str, currX + itemWidth / 3, barStart + 5);
			}
			currX += itemWidth;
		}
	}


	@Override
	protected int getItemCount() {
		return freqs.length;
	}

	@Override
	protected int getItemIndAt(int x, int y) {
		// TODO: make interactive
		return 0;
	}
}
