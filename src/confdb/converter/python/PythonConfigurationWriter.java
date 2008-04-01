package confdb.converter.python;

import confdb.converter.ConverterEngine;
import confdb.converter.ConverterException;
import confdb.converter.IConfigurationWriter;
import confdb.converter.IEDSourceWriter;
import confdb.converter.IESSourceWriter;
import confdb.converter.IESModuleWriter;
import confdb.converter.IModuleWriter;
import confdb.converter.IParameterWriter;
import confdb.converter.IPathWriter;
import confdb.converter.ISequenceWriter;
import confdb.converter.IServiceWriter;
import confdb.data.ESPreferable;
import confdb.data.IConfiguration;
import confdb.data.EDSourceInstance;
import confdb.data.ESSourceInstance;
import confdb.data.ESModuleInstance;
import confdb.data.ModuleInstance;
import confdb.data.Parameter;
import confdb.data.Path;
import confdb.data.Sequence;
import confdb.data.ServiceInstance;

public class PythonConfigurationWriter implements IConfigurationWriter 
{
	protected ConverterEngine converterEngine = null;

	public String toString( IConfiguration conf, WriteProcess writeProcess  ) throws ConverterException
	{
		String indent = "  ";
		StringBuffer str = new StringBuffer( 100000 );
		str.append( "# " + conf.name() + " V" + conf.version()
  		   + " (" + conf.releaseTag() + ")" + converterEngine.getNewline() + converterEngine.getNewline() );

		if ( writeProcess == WriteProcess.YES )
		{
			str.append( "import FWCore.ParameterSet.Config as cms\n\n" );
			str.append( "process = cms.Process( \"" + 
					conf.processName() + "\" )\n" );
		}
		else
			indent = "";

		if ( conf.psetCount() > 0 )
		{
			IParameterWriter parameterWriter = converterEngine.getParameterWriter();
			for ( int i = 0; i < conf.psetCount(); i++ )
			{
				Parameter pset = conf.pset(i);
				str.append( "process." + parameterWriter.toString( pset, converterEngine, "" ) );
			}
			str.append( "\n");
		}


		if ( conf.edsourceCount() > 0 )
		{
			IEDSourceWriter edsourceWriter = converterEngine.getEDSourceWriter();
			for ( int i = 0; i < conf.edsourceCount(); i++ )
			{
				EDSourceInstance edsource = conf.edsource(i);
				str.append( edsourceWriter.toString(edsource, converterEngine, indent ) );
			}
			str.append( "\n" );
		}

		if ( conf.essourceCount() > 0 )
		{
			IESSourceWriter essourceWriter = converterEngine.getESSourceWriter();
			for ( int i = 0; i < conf.essourceCount(); i++ )
			{
				ESSourceInstance essource = conf.essource(i);
				str.append( essourceWriter.toString(essource, converterEngine, indent ) );
			}

			for ( int i = 0; i < conf.essourceCount(); i++ )
			{
				ESSourceInstance instance = conf.essource(i);
				if ( instance instanceof ESPreferable) 
				{
					ESPreferable esp = (ESPreferable)instance;
					if ( esp.isPreferred() ) 
						str.append( "process.prefer( \"" + instance.name() + "\" )" ); 
				}
			}
			str.append( "\n");
		}

		if ( conf.esmoduleCount() > 0 )
		{
			IESModuleWriter esmoduleWriter = converterEngine.getESModuleWriter();
			for ( int i = 0; i < conf.esmoduleCount(); i++ )
			{
				ESModuleInstance esmodule = conf.esmodule(i);
				str.append( esmoduleWriter.toString( esmodule, converterEngine, "" ) );
			}
			str.append( "\n");
		}
		

		if ( conf.serviceCount() > 0 )
		{
			IServiceWriter serviceWriter = converterEngine.getServiceWriter();
			for ( int i = 0; i < conf.serviceCount(); i++ )
			{
				ServiceInstance service = conf.service(i);
				str.append( serviceWriter.toString( service, converterEngine, indent ) );
			}
			str.append( "\n");
		}

		
		if ( conf.moduleCount() > 0 )
		{
			IModuleWriter moduleWriter = converterEngine.getModuleWriter();
			for ( int i = 0; i < conf.moduleCount(); i++ )
			{
				ModuleInstance module = conf.module(i);
				str.append( moduleWriter.toString( module ) );
			}
			str.append( "\n");
		}

		if ( conf.sequenceCount() > 0 )
		{
			ISequenceWriter sequenceWriter = converterEngine.getSequenceWriter();
			for ( int i = 0; i < conf.sequenceCount(); i++ )
			{
				Sequence sequence = conf.sequence(i);
				str.append( sequenceWriter.toString(sequence, converterEngine, indent ) );
			}
			str.append( "\n");
		}

		if ( conf.pathCount() > 0 )
		{
			IPathWriter pathWriter = converterEngine.getPathWriter();
			for ( int i = 0; i < conf.pathCount(); i++ )
			{
				Path path = conf.path(i);
				str.append( pathWriter.toString( path, converterEngine, indent ) );
			}
			str.append( "\n");
		}

		return str.toString();
	}

	public void setConverterEngine( ConverterEngine converterEngine ) 
	{
		this.converterEngine = converterEngine;
	}
	
}
