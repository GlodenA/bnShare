package com.ipu.server.fileServer.elfinder.util;



import java.util.ArrayList;
import java.util.List;

import com.ipu.server.fileServer.elfinder.controller.executor.FsItemEx;
import com.ipu.server.fileServer.elfinder.service.FsItemFilter;

public abstract class FsItemFilterUtils
{
	public static FsItemFilter FILTER_ALL = new FsItemFilter()
	{
		
		public boolean accepts(FsItemEx item)
		{
			return true;
		}
	};

	public static FsItemFilter FILTER_FOLDER = new FsItemFilter()
	{
		
		public boolean accepts(FsItemEx item)
		{
			return item.isFolder();
		}
	};

	public static FsItemFilter createFileNameKeywordFilter(final String keyword)
	{
		return new FsItemFilter()
		{
			
			public boolean accepts(FsItemEx item)
			{
				return item.getName().contains(keyword);
			}
		};
	}

	public static FsItemEx[] filterFiles(FsItemEx[] sourceFiles,
			FsItemFilter filter)
	{
		List<FsItemEx> filtered = new ArrayList<FsItemEx>();
		for (FsItemEx file : sourceFiles)
		{
			if (filter.accepts(file))
				filtered.add(file);
		}

		return filtered.toArray(new FsItemEx[0]);
	}

	/**
	 * returns a FsItemFilter according to given mimeFilters
	 * 
	 * @param mimeFilters An array of MIME types, if <code>null</code> no filtering is done
	 * @return A filter that only accepts the supplied MIME types.
	 */
	public static FsItemFilter createMimeFilter(final String[] mimeFilters)
	{
		if (mimeFilters == null || mimeFilters.length == 0)
			return FILTER_ALL;

		return new FsItemFilter()
		{
			
			public boolean accepts(FsItemEx item)
			{
				String mimeType = item.getMimeType().toUpperCase();

				for (String mf : mimeFilters)
				{
					mf = mf.toUpperCase();
					if (mimeType.startsWith(mf + "/") || mimeType.equals(mf))
						return true;
				}
				return false;
			}
		};
	}

}
