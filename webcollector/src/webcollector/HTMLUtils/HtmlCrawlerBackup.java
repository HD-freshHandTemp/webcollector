package webcollector.HTMLUtils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import model.userInfo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import grab.FileWrite;
import cn.edu.hfut.dmic.contentextractor.ContentExtractor;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatum;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.net.HttpRequest;
import cn.edu.hfut.dmic.webcollector.net.HttpResponse;
import cn.edu.hfut.dmic.webcollector.plugin.berkeley.BreadthCrawler;

public class HtmlCrawlerBackup extends BreadthCrawler {
	private final static Log logger = LogFactory.getLog(HtmlCrawlerBackup.class);
	// 用于保存图片的文件夹
	String downloadDir = null;
	List<Map<String, Object>> cssList = null;
	Map<String, Object> cssmap = null;
	List<userInfo> listall = null;
	userInfo userinfo = null;
	
	
	//新爬虫
	
	
	
	
	
	
	//旧爬虫

	public HtmlCrawlerBackup(String crawlPath, String downPath) {// String crawlPath
		super(crawlPath, true);
		downloadDir = downPath + "/";

		System.setProperty("java.protocol.handler.pkgs", "javax.net.ssl");// 处理网页证书问题
		HostnameVerifier hv = new HostnameVerifier() {
			public boolean verify(String urlHostName, SSLSession session) {
				return urlHostName.equals(session.getPeerHost());
			}
		};

		HttpsURLConnection.setDefaultHostnameVerifier(hv);

		listall = new ArrayList<userInfo>();

	}

	public HttpResponse getResponse(CrawlDatum crawlDatum) {
		HttpResponse res = null;
		HttpRequest request = null;
		try {
			request = new HttpRequest(crawlDatum);

			if (request != null) {
				res = request.response();
			}

		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
		return res;
	}

	@Override
	public void visit(Page page, CrawlDatums next) {
		List<userInfo> listallnow = null;
		String contentType = null;
		// Connection connection = null;
		if (page.response() == null) {
			return;
		} else {
			contentType = page.response().contentType();
		}
		if (contentType == null) {
			return;
		} else if (contentType.contains("html")) {

			if (page != null) {
				// page.charset("UTF-8");
				/*
				 * page.getCharset()不是可以获取页面编码吗?
				 */
				Getending ending = new Getending();
				String endings = ending.getFileEncoding(page.url());
				page.charset(endings);
				// System.out.println(page.html()+"--------------------00000000000000000000");
				// System.out.println(page.html());
				// 如果是网页，则抽取其中包含图片的URL，放入后续任务
				
				
				/*
				 * 这么做会获取到很多无用的连接,建议使用WebCollector的爬虫深度来代替
				 */
				if (page.select("a[href]").size() > 0) {// 提取网页中的链接,放入后续的队列中
					Elements ahref = page.select("a[href]");
					for (Element img : ahref) {
						String hrefSrc = img.attr("abs:href");
						if (hrefSrc.indexOf("#") != -1) {
							hrefSrc = hrefSrc.substring(0,
									hrefSrc.lastIndexOf("#"));
						}
						next.add(hrefSrc);
					}
				}
				

				if (page.select("link[href]").size() > 0) {// 抓取页面上的才算是样式文件
					cssList = new ArrayList<Map<String, Object>>();
					Elements cssall = page.select("link[href]");
					for (Element cssn : cssall) {
						String cssref = cssn.attr("abs:href");
						try {
							String csscontent = Jsoup
									.connect(cssref)
									.ignoreContentType(true)
									.userAgent(
											"Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2.15)")
									.timeout(5000).get().text();
							String cssname = cssref.substring(
									cssref.lastIndexOf("/") + 1,
									cssref.length());
							if (cssname.indexOf(".css") == -1
									&& cssname.indexOf(".js") == -1) {
								String fln = cssname.replaceAll(
										"[^a-z^A-Z^0-9]", "");
								cssname = fln.replace("^", "");
								cssname = cssname + ".css";
							}
							if (cssname.indexOf(".css") != -1) {
								cssname = cssname.substring(0,
										cssname.indexOf(".css") + 4);
								// System.out.println("1--------------------------css名"+cssname);

								if (cssname.indexOf("#") != -1
										|| cssname.indexOf("?") != -1) {
									cssname = cssname.replace("#", "");
									cssname = cssname.replace("?", "");
									// System.out.println("2--------------------------css名"+cssname);
								}
							}
							String cssfiles = cssref.split("://")[1];
							String cssfile = downloadDir
									+ cssfiles.substring(0,
											cssfiles.lastIndexOf("/") + 1)
									+ cssname;
							// cssmap.put("href", cssref);
							// cssmap.put("cssconment", csscontent);
							// cssmap.put("cssname", cssname);
							cssmap = new HashMap<String, Object>();
							cssmap.put("filecss",
									downloadDir + cssref.split("://")[1]);
							cssList.add(cssmap);
							System.out
									.println("--------------------------------------csss008"
											+ cssList.size());
							FileWrite filewrite = new FileWrite();
							filewrite.bufferedWrite(cssfile, cssname,
									csscontent); // css文件下载到本地
							// System.out.println("路径css-------------"+cssfile);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}
		Getending ending = new Getending();// 解析网页的源码字符集,以源码进行解码,得到数据
		String endings = ending.getFileEncoding(page.url());
		page.charset(endings);
		String contents = null;
		String title = null;
		String url = null;

		try {
			// Element contentElement =
			// ContentExtractor.getContentElementByHtml(page.html(), url);//正文节点
			// contents=contentElement.select("div").get(0).text();
			// System.out.println(contentElement.select("ul").size()+"------------大小");
			// title=page.doc().title();
			// FileSystemOutput fsOutput=new FileSystemOutput(downloadDir);
			// //下载原始抓取的网页到本地
			// fsOutput.output(page);
			// Element contentElement =
			// ContentExtractor.getContentElementByHtml(page.html());
			url = page.url();
			// System.out.println(page.charset()+"*************************page");
			Element contentElement = ContentExtractor.getContentElementByHtml(page.html(), url);// 正文节点
			Element Elementcontent = ContentExtractor.getContentElementByUrl(url);
			
			
			// 解析页面中的图片
			Elements imgall = Elementcontent.select("img[src]");
			if (imgall.size() > 0) {
				for (Element ement : imgall) {
					// System.out.println());
					// System.out.println("页面图片--------------"+"-----------------"+"photo"+"------"+ement.attr("abs:src"));
					String ks = ement.attr("abs:src");
					String newimg = ks.split("://")[1];
					// System.out.println("---------------------"+"000000");
					URL urls = new URL(ks);
					HttpURLConnection httpConn = (HttpURLConnection) urls
							.openConnection();
					httpConn.connect();
					InputStream cin = httpConn.getInputStream();
					ByteArrayOutputStream outStream = new ByteArrayOutputStream();
					byte[] buffer = new byte[1024];
					int len = 0;
					while ((len = cin.read(buffer)) != -1) {
						outStream.write(buffer, 0, len);
					}
					cin.close();
					byte[] fileData = outStream.toByteArray();
					outStream.close();

					// System.out.println(fileData.length);

					String imgfile = downloadDir
							+ newimg.substring(0, newimg.lastIndexOf("/") + 1);
					// System.out.println(imgfile+"------------------"+"图片路径");
					String imgname = newimg.substring(
							newimg.lastIndexOf("/") + 1, newimg.length());
					if (imgname.indexOf(".") == -1) {
						String fln = imgname.replaceAll("[^a-z^A-Z^0-9]", "");
						imgname = fln.replace("^", "");
						imgname = imgname + ".jpg";
					}
					// System.out.println(imgname+"------------------"+"图片名称");
					savaImage(fileData, imgfile, imgname);
					ement.attr("src", imgfile + imgname);// 更改节点中a标签的连接地址
					// System.out.println(ks.substring(ks.lastIndexOf("/")+1,ks.length()));
					// System.out.println(ks.substring(0,ks.lastIndexOf("/")));
				}
			}
			// imgElement.parentNode()

			// contents=contentElement.select("div").get(0).text();
			title = page.doc().title();
			// System.out.println("原生态的标题 "+contentElement.select("h1").text());
			// System.out.println(title+"*************************title");

			// contents= ContentExtractor.getContentByHtml(page.html(),
			// page.url());
			/*
			 * if(contentElement.select("ul").size()>1){ String
			 * ulcalss=contentElement.select("ul").get(0).className();
			 * 
			 * //contentElement.removeClass(ulcalss);
			 * System.out.println(contents
			 * .indexOf(contentElement.select("ul").get
			 * (0).text())+"--------是否存在"); int
			 * ullength=contents.indexOf(contentElement
			 * .select("ul").get(0).text());
			 * System.out.println("子字符串---------"+contentElement
			 * .select("ul").get(0).text());
			 * System.out.println(ulcalss+"-------------ulclass");
			 * System.out.println
			 * ("-------------结果1截取长度"+(contents.length()-ullength));
			 * uljiequ=contents.substring(0,ullength);
			 * System.out.println("--------------------结果1 "
			 * +uljiequ+"--------------------结果1"); }
			 * if(contentElement.select("div").size()>0){
			 * //System.out.println(); int k=0; for(int
			 * firstdiv=1;firstdiv<contentElement
			 * .select("div").size();firstdiv++){
			 * if(contentElement.select("div")
			 * .get(firstdiv).select("a[href]").size()>0){ String
			 * divclass=contentElement.select("div").get(firstdiv).className();
			 * System
			 * .out.println(contents.indexOf(contentElement.select("div").get
			 * (firstdiv).text())+"--------是否存在1");
			 * System.out.println("子字符串1---------"
			 * +contentElement.select("div").get(firstdiv).text());
			 * System.out.println(divclass+"divclass"); //
			 * contents.replaceAll(contentElement
			 * .select("div").get(firstdiv).text(), "");
			 * //contentElement.removeClass(divclass);
			 * 
			 * k=contents.indexOf(contentElement.select("div").get(firstdiv).text
			 * ())+contentElement.select("div").get(firstdiv).text().length();
			 * 
			 * } } System.out.println("---------------------k"+k);
			 * if(uljiequ==null){ divjiequ=contents.substring(k,
			 * contents.length());} else{ divjiequ= uljiequ.substring(k,
			 * uljiequ.length()); }
			 * 
			 * System.out.println("--------------------结果2"+divjiequ+
			 * "--------------------结果2"); }
			 */
			// contents=divjiequ;
			// System.out.println(" 内容进行过滤--------------"+url);
			contents = Filtercontents(contentElement);// 对获取 内容进行过滤
			// System.out.println("过滤以后的内容------------------"+Filtercontents(contentElement).length());
			if (ChineseCharacterPhasing.isMessyCode(title) == true) {
				page.charset("GBK");
				url = page.url();
				title = page.doc().title();
				contents = Filtercontents(contentElement);
				if (ChineseCharacterPhasing.isMessyCode(title) == true) {
					title = null;
					contents = null;
				}
			}
			String ifcontents = contents.substring(0, 30);
			// System.out.println(ifcontents+"--------------------------截取后的字符串tontents");
			if (contents.indexOf("<") > 0 || contents.indexOf("div") > 0
					|| ifcontents.indexOf("<<") > 0) {
				contents = null;
			} else if (ifcontents.indexOf("|") != -1) {// 对抓取的文章内容进行判断,是否是满足 的条件
				if (searchCount("|", ifcontents) > 1) {
					contents = null;
				}
			} else if (ifcontents.indexOf(" ") != -1) {
				if (searchCount("|", ifcontents) > 1) {
					contents = null;
				} else if (ifcontents.indexOf("<") != -1
						|| ifcontents.indexOf("div") > 0
						|| ifcontents.indexOf("<<") > 0) {
					contents = null;
				} else if (ifcontents.indexOf(":") != -1
						|| ifcontents.indexOf("：") != -1) {
					if (searchCount(":", ifcontents) > 1
							|| searchCount("：", ifcontents) > 1) {
						contents = null;
					}
				} else if (ifcontents.indexOf("、") != -1
						&& ifcontents.indexOf(" ") != -1) {
					contents = null;
				}
				if (!("").equals(title) && title.length() > 0 && title != null) {// 对文章标题进行提取,过滤掉标题的后缀
					// System.out.println("titlle-------------------------------------888888"+title);
					if (title.indexOf("-") != -1) {
						// System.out.println("title截取的位置1------------"+title.indexOf("-"));
						title = title.substring(0, title.indexOf("-"));
						// System.out.println(title);
					} else if (title.indexOf("_") != -1) {
						// System.out.println("title截取的位置2------------"+title.lastIndexOf("_"));
						title = title.substring(0, title.indexOf("_"));
						// System.out.println(title);
					} else if (title.indexOf(" ") != -1) {
						title = title.replaceAll(" ", "");
					}
					if (!("").equals(contents) && contents != null
							&& contents.length() > 300) {
						if (contents.indexOf("-") != -1
								|| contents.indexOf("—") != -1) {
							contents = contents.replaceAll("-", "");
							contents = contents.replaceFirst("—", "");
						}
						// System.out.println(listall.size()+"********************************数据的总记录数");
						// System.out.println(contents+"------------title的长度");
						if (url.substring(url.length() - 1, url.length())
								.indexOf("/") == -1) {
							url = url + "/";
						}

						String filehtml = null;
						String filename = null;
						String id = null;
						String URLS = url.substring(0, url.length() - 1);
						int Lastn = URLS.lastIndexOf("/");
						String lastString = url.substring(Lastn, URLS.length());// 最后一个/后面的字符串
						String fln = lastString
								.replaceAll("[^a-z^A-Z^0-9]", "");
						filename = fln.replace("^", "");
						String newfilename = filename + ".html";
						filehtml = downloadDir
								+ URLS.replace(lastString, newfilename).split(
										"://")[1];
						id = URLS.replace(lastString, filename).split("://")[1];
						FileWrite filewrite = new FileWrite();
						String heads = "";
						if (cssList.size() > 0) {
							for (Map<String, Object> csslink : cssList) {
								heads += "<link rel='stylesheet' type='text/css' href="
										+ csslink.get("filecss") + " />";
								System.out
										.println("--------------------------css大小"
												+ cssList);
							}
						}
						String head = heads;
						String HTMLcontent = null;
						HTMLcontent = head
								+ Elementcontent.parentNode().outerHtml();
						filewrite
								.bufferedWrite(filehtml, filename, HTMLcontent);
						System.out.println(contentElement.parents());
						if (listall.size() == 0) {
							userinfo = new userInfo();
							userinfo.setFilehtml(filehtml);
							userinfo.setTitle(title);
							userinfo.setUrls(url);
							userinfo.setHtmls(contents);
							userinfo.setId(id);
							listall.add(userinfo);
						} else {
							for (int i = 0; i < listall.size(); i++) {
								if (!url.equals(listall.get(i).getUrls())) {
									listallnow = new ArrayList<userInfo>();
									userinfo = new userInfo();
									userinfo.setFilehtml(filehtml);
									userinfo.setTitle(title);
									userinfo.setUrls(url);
									userinfo.setId(id);
									userinfo.setHtmls(contents);
									listallnow.add(userinfo);
								}
							}
							listall.addAll(listallnow);

							System.out.println(listall.size()
									+ "----------------------------数据的总记录数");
						}
					}
				}
			}
		} catch (Exception e) {
			logger.debug(e.getMessage());
		}
	}

	public List<userInfo> getlistall() {// 返回抓取信息的集合
		System.out.println(listall.size());
		return listall;

	}

	public String Filtercontents(Element contentElement) {// 对抓取的文章内容进行过滤的方法
		String uljiequ = null;
		String divjiequ = null;
		String contents = contentElement.select("div").get(0).text();
		if (contentElement.select("ul").size() > 1) {
			int ullength = contents.indexOf(contentElement.select("ul").get(0)
					.text());
			uljiequ = contents.substring(0, ullength);
		}
		if (contentElement.select("div").size() > 0) {
			// System.out.println();
			int k = 0;
			for (int firstdiv = 1; firstdiv < contentElement.select("div")
					.size(); firstdiv++) {
				if (contentElement.select("div").get(firstdiv)
						.select("a[href]").size() > 0) {
					k = contents.indexOf(contentElement.select("div")
							.get(firstdiv).text())
							+ contentElement.select("div").get(firstdiv).text()
									.length();
				}
			}
			if (uljiequ == null) {
				divjiequ = contents.substring(k, contents.length());
			} else {
				divjiequ = uljiequ.substring(k, uljiequ.length());
			}

			if (divjiequ.substring(divjiequ.length() - 50, divjiequ.length())
					.indexOf(" ") != -1) {
				int firstkg = 50 - divjiequ.substring(divjiequ.length() - 50,
						divjiequ.length()).indexOf(" ");
				divjiequ = divjiequ.substring(0, divjiequ.length() - firstkg);
			}
		}
		return divjiequ;

	}

	public int searchCount(String shortStr, String longStr) {// 对文章内容过滤的工具方法
		// 定义一个count来存放字符串出现的次数
		int count = 0;
		// 调用String类的indexOf(String str)方法，返回第一个相同字符串出现的下标
		while (longStr.indexOf(shortStr) != -1) {
			// 如果存在相同字符串则次数加1
			count++;
			// 调用String类的substring(int beginIndex)方法，获得第一个相同字符出现后的字符串
			longStr = longStr.substring(longStr.indexOf(shortStr)
					+ shortStr.length());

		}
		// 返回次数
		return count;
	}

	public void savaImage(byte[] img, String filePath, String fileName) {
		// System.out.println("------------"+"开始下载");
		BufferedOutputStream bos = null;
		FileOutputStream fos = null;
		// System.out.println(filePath);
		File dir = new File(filePath + fileName);
		File f1 = new File(filePath);
		try {
			// 判断文件目录是否存在
			if (!dir.exists()) {
				f1.mkdirs();
			}
			fos = new FileOutputStream(dir);
			bos = new BufferedOutputStream(fos);
			bos.write(img);
			bos.flush();
			// System.out.println(filePath+fileName);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

}