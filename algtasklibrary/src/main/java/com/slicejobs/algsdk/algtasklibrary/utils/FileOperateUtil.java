package com.slicejobs.algsdk.algtasklibrary.utils;

import android.content.Context;

import com.slicejobs.algsdk.algtasklibrary.R;
import com.slicejobs.algsdk.algtasklibrary.R2;

import java.io.File;
import java.io.FilenameFilter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @ClassName: FileOperateUtil
 * @Description:  文件操作工具类
 *
 */
public class FileOperateUtil {
	public final static String TAG="FileOperateUtil";

	public final static int ROOT=0;//根目录
	public final static int TYPE_IMAGE=1;//图片
	public final static int TYPE_THUMBNAIL=2;//缩略图
	public final static int TYPE_VIDEO=3;//视频

	/**
	 *获取文件夹路径
	 * @param type 文件夹类别
	 * @param rootPath 根目录文件夹名字 为业务流水号
	 * @return
	 */
	public static String getFolderPath(Context context, int type, String rootPath) {
		//本业务文件主目录
		StringBuilder pathBuilder=new StringBuilder();
		//添加应用存储路径
		pathBuilder.append(rootPath);

		pathBuilder.append(File.separator);
		switch (type) {
			case TYPE_IMAGE:
				pathBuilder.append(context.getString(R.string.Image));
				break;
			case TYPE_VIDEO:
				pathBuilder.append(context.getString(R.string.Video));
				break;
			case TYPE_THUMBNAIL:
				pathBuilder.append(context.getString(R.string.Thumbnail));
				break;
			default:
				break;
		}
		return pathBuilder.toString();
	}

	/**
	 * 获取目标文件夹内指定后缀名的文件数组,按照修改日期排序
	 * @param file 目标文件夹路径
	 * @param content 包含的内容,用以查找视频缩略图
	 * @return
	 */
	public static List<File> listFiles(String file, final String format, String content){
		return listFiles(new File(file), format,content);
	}
	public static List<File> listFiles(String file, final String format){
		return listFiles(new File(file), format,null);
	}
	/**
	 * 获取目标文件夹内指定后缀名的文件数组,按照修改日期排序
	 * @param file 目标文件夹
	 * @param extension 指定后缀名
	 * @param content 包含的内容,用以查找视频缩略图
	 * @return
	 */
	public static List<File> listFiles(File file, final String extension, final String content){
		File[] files=null;
		if(file==null||!file.exists()||!file.isDirectory())
			return null;
		files=file.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File arg0, String arg1) {
				// TODO Auto-generated method stub
				if(content==null||content.equals(""))
					return arg1.endsWith(extension);
				else {
					return  arg1.contains(content)&&arg1.endsWith(extension);
				}
			}
		});
		if(files!=null){
			List<File> list=new ArrayList<File>(Arrays.asList(files));
			sortList(list, false);
			return list;
		}
		return null;
	}

	/**
	 *  根据修改时间为文件列表排序
	 *  @param list 排序的文件列表
	 *  @param asc  是否升序排序 true为升序 false为降序
	 */
	public static void sortList(List<File> list, final boolean asc){
		//按修改日期排序
		Collections.sort(list, new Comparator<File>() {
			public int compare(File file, File newFile) {
				if (file.lastModified() > newFile.lastModified()) {
					if(asc){
						return 1;
					}else {
						return -1;
					}
				} else if (file.lastModified() == newFile.lastModified()) {
					return 0;
				} else {
					if(asc){
						return -1;
					}else {
						return 1;
					}
				}

			}
		});
	}

	/**
	 *
	 * @param extension 后缀名 如".jpg"
	 * @return
	 */
	public static String createFileNmae(String extension){
		DateFormat format = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
		// 转换为字符串
		String formatDate = format.format(new Date());
		//查看是否带"."
		if(!extension.startsWith("."))
			extension="."+extension;
		return formatDate+extension;
	}

	/**
	 *  删除缩略图 同时删除源图或源视频
	 *  @param thumbPath 缩略图路径
	 *  @return
	 */
	public static boolean deleteThumbFile(String thumbPath, Context context) {
		boolean flag = false;

		File file = new File(thumbPath);
		if (!file.exists()) { // 文件不存在直接返回
			return flag;
		}

		flag = file.delete();
		//源文件路径
		String sourcePath=thumbPath.replace(context.getString(R.string.Thumbnail),
				context.getString(R.string.Image));
		file = new File(sourcePath);
		if (!file.exists()) { // 文件不存在直接返回
			return flag;
		}
		flag = file.delete();
		return flag;
	}
	/**
	 *根据略缩图删除视屏或者图片原文件,当你删除的是图片,thumbPath是图片路径.当你删除的是视频,thumbPath是视屏缩略图的路径
	 *  @param thumbPath 缩略图路径
	 *  @return
	 */
	public static boolean deleteSourceFile(String thumbPath, Context context) {
		boolean flag = false;
		File file = new File(thumbPath);
		if (!file.exists()) { // 文件不存在直接返回
			return flag;
		}
		flag = file.delete();//删除缩略图
		String sourcePath;
		if (thumbPath.contains(context.getString(R.string.vide_identify))) {//这是一个视频
			//进行路径替换
			sourcePath=thumbPath.replace(context.getResources().getString(R.string.Thumbnail), context.getResources().getString(R.string.Video));
			sourcePath=sourcePath.replace(".jpg", ".3gp");
		} else {//这是一张图片文件
			sourcePath = thumbPath.replace(context.getString(R.string.Image), context.getString(R.string.Thumbnail));
		}
		file = new File(sourcePath);
		if (!file.exists()) { // 文件不存在直接返回
			return flag;
		}
		flag = file.delete();
		return flag;
	}

	/**
	 *  更具原视屏路径,删除原视频, 删除缩略图
	 *  @param sourcePath 缩略图路径
	 *  @return
	 */
	public static boolean deleteVideoFile(String sourcePath, Context context) {
		boolean flag = false;

		File file = new File(sourcePath);
		if (!file.exists()) { // 文件不存在直接返回
			return flag;
		}

		flag = file.delete();//删掉视频
		//缩略图文件路径
		String thumbPath=sourcePath.replace(context.getResources().getString(R.string.Video), context.getResources().getString(R.string.Thumbnail));
		thumbPath=thumbPath.replace(".3gp", ".jpg");
		file = new File(thumbPath);//删除缩图
		if (!file.exists()) { // 文件不存在直接返回
			return flag;
		}
		flag = file.delete();
		return flag;
	}

}