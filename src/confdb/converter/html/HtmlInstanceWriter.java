package confdb.converter.html;

import confdb.converter.ascii.AsciiInstanceWriter;
import confdb.data.Instance;

public class HtmlInstanceWriter extends AsciiInstanceWriter 
{	
	@Override
	protected String getName(Instance instance) 
	{
		return "<b>" + instance.name() + "</b>";
	}

	@Override
	protected String getTemplateName( Instance instance ) 
	{
		String type = instance.template().name();

		String release = instance.config().releaseTag();

		return "<a href=\"javascript:showSource('" + release + "','" + type + "')\">" + type + "</a>";
	}
	
	
}
