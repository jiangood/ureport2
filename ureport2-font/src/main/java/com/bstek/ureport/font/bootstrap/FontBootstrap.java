/*******************************************************************************
 * Copyright 2017 Bstek
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package com.bstek.ureport.font.bootstrap;

import com.bstek.ureport.UReportEngine;
import com.bstek.ureport.bootstrap.UReportBootstrap;
import com.bstek.ureport.export.pdf.font.FontBootstrapContext;
import com.bstek.ureport.export.pdf.font.FontRegister;
import com.bstek.ureport.font.arial.ArialFontRegister;
import com.bstek.ureport.font.comicsansms.ComicSansMSFontRegister;
import com.bstek.ureport.font.couriernew.CourierNewFontRegister;
import com.bstek.ureport.font.fangsong.FangSongFontRegister;
import com.bstek.ureport.font.heiti.HeiTiFontRegister;
import com.bstek.ureport.font.kaiti.KaiTiFontRegister;
import com.bstek.ureport.font.songti.SongTiFontRegister;
import com.bstek.ureport.font.timesnewroman.TimesNewRomanFontRegister;
import com.bstek.ureport.font.yahei.YaheiFontRegister;

public class FontBootstrap implements UReportBootstrap {
	@Override
	public int order() { return 10; }
	@Override
	public void bootstrap(UReportEngine engine) {
		FontRegister[] registers = new FontRegister[] {
			new ArialFontRegister(),
			new ComicSansMSFontRegister(),
			new CourierNewFontRegister(),
			new FangSongFontRegister(),
			new HeiTiFontRegister(),
			new KaiTiFontRegister(),
			new SongTiFontRegister(),
			new TimesNewRomanFontRegister(),
			new YaheiFontRegister(),
		};
		for (FontRegister r : registers) {
			FontBootstrapContext.register(r);
		}
	}
}
