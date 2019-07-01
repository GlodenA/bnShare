package com.ipu.server.fileServer.elfinder.impl;



import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import sun.misc.BASE64Decoder;

import com.ipu.server.fileServer.elfinder.controller.executor.FsItemEx;
import com.ipu.server.fileServer.elfinder.localfs.LocalFsVolume;
import com.ipu.server.fileServer.elfinder.service.FsItem;
import com.ipu.server.fileServer.elfinder.service.FsItemFilter;
import com.ipu.server.fileServer.elfinder.service.FsSecurityChecker;
import com.ipu.server.fileServer.elfinder.service.FsService;
import com.ipu.server.fileServer.elfinder.service.FsServiceConfig;
import com.ipu.server.fileServer.elfinder.service.FsVolume;

public class DefaultFsService implements FsService
{
	FsSecurityChecker _securityChecker;

	FsServiceConfig _serviceConfig;

	Map<String, FsVolume> _volumeMap = new HashMap<String, FsVolume>();

	// special characters should be encoded, avoid to be processed as a part of
	// URL
	String[][] escapes = { { "+", "_P" }, { "-", "_M" }, { "/", "_S" },
			{ ".", "_D" }, { "=", "_E" } };

	
	/**
	 * find files by name pattern, this provides a simple recursively iteration based method
	 * lucene engines can be introduced to improve it!
	 * This searches across all volumes.
	 * 
	 * @param filter The filter to apply to select files.
	 * @return A collection of files that match  the filter and gave the root as a parent.
	 */
	public FsItemEx[] find(FsItemFilter filter)
	{
		List<FsItemEx> results = new ArrayList<FsItemEx>();
		for (FsVolume vol : _volumeMap.values())
		{
			FsItem root = vol.getRoot();
			results.addAll(findRecursively(filter, root));
		}

		return results.toArray(new FsItemEx[0]);
	}

	/**
	 * find files recursively in specific folder
	 * 
	 * @param filter
	 *            The filter to apply to select files.
	 * @param root
	 *            The location in the hierarchy to search from.
	 * @return A collection of files that match the filter and have the root as
	 *         a parent.
	 */
	private Collection<FsItemEx> findRecursively(FsItemFilter filter,
			FsItem root)
	{
		List<FsItemEx> results = new ArrayList<FsItemEx>();
		FsVolume vol = root.getVolume();
		for (FsItem child : vol.listChildren(root))
		{
			if (vol.isFolder(child))
			{
				results.addAll(findRecursively(filter, child));
			}
			else
			{
				FsItemEx item = new FsItemEx(child, this);
				if (filter.accepts(item))
					results.add(item);
			}
		}

		return results;
	}

	
	public FsItem fromHash(String hash)
	{
		for (FsVolume v : _volumeMap.values())
		{
			String prefix = getVolumeId(v) + "_";

			if (hash.equals(prefix))
			{
				return v.getRoot();
			}

			if (hash.startsWith(prefix))
			{
				String localHash = hash.substring(prefix.length());

				for (String[] pair : escapes)
				{
					localHash = localHash.replace(pair[1], pair[0]);
				}
				 
				String relativePath = new String(getEncodeBase64(localHash));
				return v.fromPath(relativePath);
//				return v.fromPath("");
			}
		}

		return null;
	}

	

	private String getEncodeBase64(String s) {
		BASE64Decoder decoder = new BASE64Decoder(); 
		try { 
			byte[] b = decoder.decodeBuffer(s); 
			return new String(b); 
			} catch (Exception e) { 
			return null; 
			} 
		
	}

	public String getHash(FsItem item) throws IOException
	{
		String relativePath = item.getVolume().getPath(item);
		String base = new String(Base64.encodeBase64(relativePath.getBytes()));

		for (String[] pair : escapes)
		{
			base = base.replace(pair[0], pair[1]);
		}

		return getVolumeId(item.getVolume()) + "_" + base;
	}

	public FsSecurityChecker getSecurityChecker()
	{
		return _securityChecker;
	}

	public FsServiceConfig getServiceConfig()
	{
		return _serviceConfig;
	}

	
	public String getVolumeId(FsVolume volume)
	{
		for (Entry<String, FsVolume> en : _volumeMap.entrySet())
		{
			if (en.getValue() == volume)
				return en.getKey();
		}

		return null;
	}

	public Map<String, FsVolume> getVolumeMap()
	{
		return _volumeMap;
	}

	public FsVolume[] getVolumes()
	{
		return _volumeMap.values().toArray(new FsVolume[0]);
	}

	public void setSecurityChecker(FsSecurityChecker securityChecker)
	{
		_securityChecker = securityChecker;
	}

	public void setServiceConfig(FsServiceConfig serviceConfig)
	{
		_serviceConfig = serviceConfig;
	}

	public void setVolumeMap(Map<String, FsVolume> volumeMap)
	{
		for (Entry<String, FsVolume> en : volumeMap.entrySet())
		{
			addVolume(en.getKey(), en.getValue());
		}
	}

	/**
	 * @deprecated {@link #setVolumeMap(Map)}
	 * @param volumes
	 *            The volumes available.
	 * @throws IOException
	 *             If there is a problem with using one of the volumes.
	 */
	public void setVolumes(FsVolume[] volumes) throws IOException
	{
		Logger.getLogger(getClass())
				.warn("calling setVolumes() is deprecated, please use setVolumeMap() to specify volume id explicitly");
		char vid = 'A';
		for (FsVolume volume : volumes)
		{
			_volumeMap.put("" + vid, volume);
			Logger.getLogger(this.getClass()).info(
					String.format("mounted %s: %s", "" + vid, volume));
			vid++;
		}
	}

	public void addVolume(String name, FsVolume fsVolume)
	{
		_volumeMap.put(name, fsVolume);
		Logger.getLogger(this.getClass()).info(
				String.format("mounted %s: %s", name, fsVolume));
	}
}
