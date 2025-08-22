import sketchingpy

WIDTH = 1000
HEIGHT = 800
BACKGROUND_COLOR = '#FFFFFF'
FONT = 'IBMPlexMono-Regular'
LARGE_SIZE = 16
SMALL_SIZE = 12
LEFT_PAD = 60
INDUSTRY_PAD = LEFT_PAD + 180
RIGHT_PAD = 60
TOP_PAD = 40
BOTTOM_PAD = 20
Y_AXIS_PAD = 100
LEN_Y_AXIS = HEIGHT * (3/8)
LEGEND_WIDTH = 115
GRAPH_LEGEND_BUFFER = 0
LEN_X_AXIS = WIDTH - (INDUSTRY_PAD + Y_AXIS_PAD + LEGEND_WIDTH + RIGHT_PAD + GRAPH_LEGEND_BUFFER)
AXIS_LABEL_PAD = 35
TITLE_PAD = 20
TICK_SIZE = 5
BOTTOM_SECTION_OFFSET = 75


EDUCATION = {'Professional Degree': '#0c2c84', "Master's": '#225ea8', "Bachelor's": '#1d91c0', 'Associate Degree': '#41b6c4', 
'Some College': '#7fcdbb', 'Secondary School': '#c7e9b4'}

INDUSTRIES = ['Banking/Financial Services','Computer Systems Design & Services','Energy','Fintech','Government',
'Healthcare','Higher Education','Insurance','Internet/Telecomm/Info Services','Manufacturing','Media & Advertising Services',
'Retail & Consumer Services','Software Development','Transportation/Supply Chain']

LANG_COLS = ['Lang_Ada','Lang_Apex','Lang_Assembly','Lang_Bash/Shell (all shells)','Lang_C','Lang_C#','Lang_C++',
'Lang_Clojure','Lang_Cobol','Lang_Crystal','Lang_Dart','Lang_Delphi','Lang_Elixir','Lang_Erlang','Lang_F#','Lang_Fortran',
'Lang_GDScript','Lang_Go','Lang_Groovy','Lang_HTML/CSS','Lang_Haskell','Lang_Java','Lang_JavaScript','Lang_Julia',
'Lang_Kotlin','Lang_Lisp','Lang_Lua','Lang_MATLAB','Lang_MicroPython','Lang_Nim','Lang_OCaml','Lang_Objective-C','Lang_PHP',
'Lang_Perl','Lang_PowerShell','Lang_Prolog','Lang_Python','Lang_R','Lang_Ruby','Lang_Rust','Lang_SQL','Lang_Scala',
'Lang_Solidity','Lang_Swift','Lang_TypeScript','Lang_VBA','Lang_Visual Basic (.Net)','Lang_Zephyr','Lang_Zig']

EDITOR_COLS = ['Editor_Android Studio','Editor_BBEdit','Editor_CLion','Editor_Code::Blocks','Editor_DataGrip',
'Editor_Eclipse','Editor_Emacs','Editor_Fleet','Editor_Geany','Editor_Goland','Editor_Helix','Editor_IPython',
'Editor_IntelliJ IDEA','Editor_Jupyter Notebook/JupyterLab','Editor_Kate','Editor_Nano','Editor_Neovim','Editor_Netbeans',
'Editor_Notepad++','Editor_PhpStorm','Editor_PyCharm','Editor_Qt Creator','Editor_RStudio',
'Editor_Rad Studio (Delphi, C++ Builder)','Editor_Rider','Editor_RubyMine','Editor_Spacemacs','Editor_Spyder',
'Editor_Sublime Text','Editor_VSCodium','Editor_Vim','Editor_Visual Studio','Editor_Visual Studio Code','Editor_WebStorm',
'Editor_Xcode']

AI_COLS = ['AI_Amazon Q','AI_Andi','AI_AskCodi','AI_Bing AI','AI_ChatGPT','AI_Claude','AI_Codeium','AI_Cody',
'AI_GitHub Copilot','AI_Google Gemini','AI_Lightning AI','AI_Meta AI','AI_Metaphor','AI_Neeva AI','AI_OpenAI Codex',
'AI_Perplexity AI','AI_Phind','AI_Quora Poe','AI_Replit Ghostwriter','AI_Snyk Code','AI_Tabnine',
'AI_Visual Studio Intellicode','AI_Whispr AI','AI_WolframAlpha','AI_You.com']

class SurveyDashboardPresenter:
    def __init__(self, records):
        self._records = records
        self._selected_index = 0
        self._selected_industry = INDUSTRIES[0]
        self._hover_index = None
        self._static_ymax = 200000
        self._static_xmax = 30
        self._bottom_top3   = []
        self._selected_bottom = [0, 0, 0]
        self._hover_bottom    = [None, None, None] 
        self._hover_edu = None

    def draw(self, sketch):
        self._filtered = [r for r in self._records if r['Industry'] == self._selected_industry]
        self._compute_big_stats()
        self._compute_bottom_stats()
        sketch.clear(BACKGROUND_COLOR)
        self._draw_background_area(sketch)
        self._draw_title(sketch)
        self._draw_industry_selector(sketch)
        self._draw_big_chart(sketch)
        self._draw_axis_labels(sketch)
        self._draw_legend(sketch)
        self._draw_section_header(sketch)
        self._draw_bottom_axes(sketch)

    def _draw_background_area(self, sketch):
        sketch.push_style()
        sketch.clear_stroke()
        sketch.set_fill('#EEEEEE')
        sketch.set_stroke_weight(3)
        sketch.draw_rect(INDUSTRY_PAD, TOP_PAD, WIDTH, HEIGHT)
        sketch.set_stroke('#000000')
        sketch.draw_line(0, TOP_PAD, WIDTH, TOP_PAD)
        sketch.clear_stroke()
        sketch.pop_style()

    def _draw_industry_selector(self, sketch):
        sketch.push_style()
        sketch.set_text_font(FONT, LARGE_SIZE)
        sketch.set_stroke_weight(2)
        sketch.set_fill('#000000')
        sketch.set_text_align('center', 'center')
        sketch.draw_text(INDUSTRY_PAD/2, TOP_PAD + 20, "Select an Industry")
        sketch.pop_style()
        top_y = TOP_PAD + 60
        bottom_y = HEIGHT - BOTTOM_PAD - 6
        step = (bottom_y - top_y) / (len(INDUSTRIES) - 1)

        sketch.push_style()
        sketch.set_text_font(FONT, SMALL_SIZE)
        sketch.set_text_align('right', 'center')
        for i, industry in enumerate(INDUSTRIES):
            y = top_y + i * step
            if industry == self._selected_industry:
                sketch.push_style()
                sketch.clear_stroke()
                sketch.set_fill('#EEEEEE')
                sketch.set_rect_mode('radius')
                sketch.draw_rect(INDUSTRY_PAD/2, y, INDUSTRY_PAD/2, step/2)
                sketch.pop_style()
                sketch.set_stroke('#000000')
                sketch.set_stroke_weight(2)
                sketch.set_text_font(FONT, SMALL_SIZE-1)
            elif self._hover_index == i:
                sketch.set_stroke('#000000')
                sketch.set_stroke_weight(2)
                sketch.set_text_font(FONT, SMALL_SIZE-1)
            else:
                sketch.set_stroke('#555555')
                sketch.set_stroke_weight(1)
                sketch.set_text_font(FONT, SMALL_SIZE-1)
            sketch.draw_text(INDUSTRY_PAD - 10, y, industry)
        sketch.pop_style()

    def _draw_title(self, sketch):
        sketch.push_style()
        sketch.set_text_font(FONT, LARGE_SIZE + 3)
        sketch.set_stroke_weight(2)
        sketch.set_fill('#000000')
        sketch.set_text_align('center', 'center')
        sketch.draw_text(WIDTH/2, TOP_PAD/2, 'Stack Overflow Developer Survey Dashboard')
        sketch.pop_style()

    def _draw_big_chart(self, sketch):
        x0, y0 = INDUSTRY_PAD + Y_AXIS_PAD, TOP_PAD + 40
        bottom_y = y0 + LEN_Y_AXIS
        right_x = x0 + LEN_X_AXIS
        y_ticks = [self._static_ymax / 5 * i for i in range(1,6)]

        # axes
        sketch.push_style()
        sketch.set_stroke_weight(2)
        sketch.draw_line(x0, y0,    x0, bottom_y)
        sketch.draw_line(x0, bottom_y, right_x, bottom_y)
        sketch.pop_style()

        # Y‐ticks + labels
        sketch.push_style()
        sketch.set_text_font(FONT, SMALL_SIZE)
        sketch.set_fill('#000000')
        sketch.set_text_align('right', 'center')
        for i in range(1,6):
            y = bottom_y - i * (LEN_Y_AXIS / 5)
            sketch.set_stroke_weight(2)
            sketch.draw_line(x0 - TICK_SIZE, y, x0 + TICK_SIZE, y)
            sketch.set_stroke_weight(1)
            sketch.draw_text(x0 - TICK_SIZE - 5, y, str(int(y_ticks[i-1]/1000)))
        sketch.pop_style()

        # X‐ticks + labels
        labels = [0, 5, 10, 15, 20, 25, '30+']
        n = len(labels) - 1
        sketch.push_style()
        sketch.set_text_font(FONT, SMALL_SIZE)
        sketch.set_fill('#000000')
        sketch.set_text_align('center', 'top')
        for idx, lab in enumerate(labels):
            x = x0 + (idx / n) * LEN_X_AXIS
            sketch.set_stroke_weight(2)
            sketch.draw_line(x, bottom_y - TICK_SIZE, x, bottom_y + TICK_SIZE)
            sketch.set_stroke_weight(1)
            sketch.draw_text(x, bottom_y + TICK_SIZE + 5, str(lab))
        sketch.pop_style()

        # weighted‐fit lines only
        sketch.push_style()
        sketch.set_stroke_weight(3)
        for ed, (m, b) in self._big_fit.items():
            col = EDUCATION[ed]
            if self._hover_edu and ed != self._hover_edu:
                col = '#DDDDDD'
            sketch.set_stroke(col)
            y1 = bottom_y - b * self._yscale
            y2 = bottom_y - (b + m * self._big_xmax) * self._yscale
            y1 = min(max(y1, y0), bottom_y)
            y2 = min(max(y2, y0), bottom_y)
            sketch.draw_line(x0, y1, right_x, y2)
        sketch.pop_style()
    
    def _draw_axis_labels(self, sketch):
        sketch.push_style()
        sketch.set_text_font(FONT, SMALL_SIZE)
        sketch.set_fill('#000000')
        sketch.set_text_align('center', 'center')
        
        x0 = INDUSTRY_PAD + Y_AXIS_PAD
        x1 = WIDTH - (RIGHT_PAD + LEGEND_WIDTH + GRAPH_LEGEND_BUFFER)
        x_center = (x0 + x1) / 2
        y_pos = TOP_PAD + LEN_Y_AXIS + 40 + AXIS_LABEL_PAD
        sketch.draw_text(x_center, y_pos, 'Years of Coding Experience')
        
        chart_top = TOP_PAD + 40
        y_center = chart_top + LEN_Y_AXIS / 2
        label_x = x0 - AXIS_LABEL_PAD - 20
        
        sketch.push_transform()
        sketch.translate(label_x, y_center)
        sketch.rotate(-90)
        sketch.draw_text(0, 0, 'Median Salary (thousands of $)')
        sketch.pop_transform()
        
        title_x = x0 + LEN_X_AXIS/2
        title_y = chart_top - TITLE_PAD
        sketch.set_text_font(FONT, LARGE_SIZE)
        sketch.draw_text(title_x, title_y, 'Median Salary vs Coding Experience')
        
        sketch.pop_style()
    
    def _draw_legend(self, sketch):
        chart_top_y  = TOP_PAD + 40
        chart_height = HEIGHT * (3/8)
        legend_x = WIDTH - RIGHT_PAD/2 - LEGEND_WIDTH
        legend_y = chart_top_y

        # draw the surrounding box
        sketch.push_style()
        sketch.set_rect_mode('corner')
        sketch.set_fill('#EEEEEE')
        sketch.set_stroke('#000000')
        sketch.set_stroke_weight(1.5)
        sketch.draw_rect(legend_x, legend_y, LEGEND_WIDTH, chart_height)
        sketch.pop_style()

        # dots + text
        n = len(EDUCATION)
        pad_v = 25
        usable = chart_height - 2*pad_v
        spacing = usable / (n - 1)

        sketch.push_style()
        sketch.set_text_font(FONT, SMALL_SIZE)
        sketch.set_text_align('left', 'center')
        sketch.set_ellipse_mode('radius')
        for i, (label, color) in enumerate(EDUCATION.items()):
            y = legend_y + pad_v + i*spacing
            
            # draw the dot
            sketch.push_style()
            sketch.clear_stroke()
            sketch.set_fill(color)
            sketch.draw_ellipse(legend_x + 10, y, 5, 5)
            sketch.pop_style()
            
            # draw the label
            if ' ' in label:
                first, second = label.split(' ', 1)
                line_off = SMALL_SIZE * 0.6
                sketch.push_style()
                fill = '#000000' if (not self._hover_edu or label == self._hover_edu) else '#888888'
                sketch.set_fill(fill)
                sketch.draw_text(legend_x + 20, y - line_off, first)
                sketch.draw_text(legend_x + 20, y + line_off, second)
                sketch.pop_style()
            else:
                sketch.push_style()
                fill = '#000000' if (not self._hover_edu or label == self._hover_edu) else '#888888'
                sketch.set_fill(fill)
                sketch.draw_text(legend_x + 20, y, label)
                sketch.pop_style()
        sketch.pop_style()

    def _draw_section_header(self, sketch):
        sketch.push_style()
        sketch.set_text_font(FONT, LARGE_SIZE + 2)
        sketch.set_fill('#000000')
        sketch.set_text_align('center', 'center')
        x0 = INDUSTRY_PAD + 30
        x1 = WIDTH - 30
        x_center = (x0 + x1) / 2
        y_label = TOP_PAD + LEN_Y_AXIS + AXIS_LABEL_PAD
        y_header = y_label + BOTTOM_SECTION_OFFSET
        sketch.draw_text(x_center, y_header, 'Top 3 Most Popular Technologies')
        left = x0
        right = WIDTH - RIGHT_PAD/2
        sketch.set_stroke('#000000')
        sketch.set_stroke_weight(1)
        sketch.draw_line(left, y_header - SMALL_SIZE - 3, right, y_header - SMALL_SIZE - 3)
        sketch.draw_line(left, y_header + SMALL_SIZE, right, y_header + SMALL_SIZE)
        sketch.pop_style()
    
    def _draw_bottom_axes(self, sketch):
        x0 = INDUSTRY_PAD + Y_AXIS_PAD
        chart_right = WIDTH - RIGHT_PAD/2
        full_w = chart_right - x0
        slot_w = full_w / 3 - 30
        header_y = 545
        top_y = header_y + SMALL_SIZE + 10
        height = LEN_Y_AXIS * 2/3
        bottom_y = top_y + height
        CATEGORY_LABELS = ['Coding Languages', 'Code Editors', 'AI Tools']
        LENGTHS = [160, 120, 85]
        
        sketch.push_style()
        sketch.set_stroke('#000000')
        sketch.set_stroke_weight(2)
        
        for i in range(3):
            xs = x0 + i * (slot_w + 45)
            xe = xs + slot_w
            # category title above the tech names
            sketch.push_style()
            sketch.set_text_font(FONT, SMALL_SIZE + 4)
            sketch.clear_stroke()
            sketch.set_fill('#000000')
            sketch.set_text_align('center', 'bottom')
            cat_y = header_y - 40 - (SMALL_SIZE + 4)
            sketch.draw_text(xs + slot_w/2, cat_y, CATEGORY_LABELS[i])
            length = LENGTHS[i]
            x_center = xs + slot_w/2
            y_line = cat_y + 3
            
            sketch.set_stroke('#000000')
            sketch.set_stroke_weight(1)
            sketch.draw_line(x_center - length/2, y_line, x_center + length/2, y_line) 
            sketch.pop_style()
            
            # draw the 3 stacked tech labels
            top3 = self._bottom_top3[i]
            sketch.push_style()
            sketch.set_text_font(FONT, SMALL_SIZE)
            sketch.set_text_align('center', 'center')
            for j, tech in enumerate(top3):
                yj = header_y + j * (SMALL_SIZE + 4) - 35
                if j == self._selected_bottom[i] or j == self._hover_bottom[i]:
                    sketch.set_stroke_weight(2)
                else:
                    sketch.set_stroke_weight(1)
                label = tech.split('_', 1)[1]
                sketch.draw_text(xs + slot_w/2, yj, label)
            sketch.pop_style()
            
            # axes lines
            sketch.draw_line(xs, top_y - 15, xs, bottom_y)
            sketch.draw_line(xs, bottom_y, xe, bottom_y)
            
            # y-labels on first graph only
            if i == 0:
                n_lbl = len(EDUCATION)
                vsp = height / (n_lbl - 1) - 3
                sketch.push_style()
                sketch.clear_stroke()
                sketch.set_text_font(FONT, SMALL_SIZE - 1)
                sketch.set_text_align('right', 'center')
                sketch.set_fill('#000000')
                for k, (label, _) in enumerate(EDUCATION.items()):
                    y = top_y + k * vsp
                    if ' ' in label:
                        a, b = label.split(' ', 1)
                        off = SMALL_SIZE * 0.6
                        sketch.draw_text(xs - TICK_SIZE - 2, y - off, a)
                        sketch.draw_text(xs - TICK_SIZE - 2, y + off, b)
                    else:
                        sketch.draw_text(xs - TICK_SIZE - 2, y, label)
                sketch.pop_style()
            
            # bars for the single selected tech
            sel_idx = self._selected_bottom[i]
            sel_tech = top3[sel_idx]
            pct = self._bottom_data[i][sel_tech]
            bar_h = height / len(EDUCATION) * 0.6
            for k, ed in enumerate(EDUCATION):
                y = top_y + k * (height / (len(EDUCATION) - 1) - 3) - bar_h / 2
                w = pct.get(ed, 0) * (slot_w / 100)
                sketch.push_style()
                color = EDUCATION[ed]
                if self._hover_edu and ed != self._hover_edu:
                    color = '#DDDDDD'
                sketch.set_fill(color)
                sketch.clear_stroke()
                sketch.draw_rect(xs + 1, y, w, bar_h)
                sketch.pop_style()
                
            # x-ticks + percent labels
            n_x = 4
            sketch.push_style()
            sketch.set_text_font(FONT, SMALL_SIZE)
            sketch.set_text_align('center', 'top')
            for j in range(n_x):
                xt = xs + (j + 1) * slot_w / n_x
                sketch.draw_line(xt, bottom_y - TICK_SIZE, xt, bottom_y + TICK_SIZE)
                sketch.set_stroke('#EEEEEE')
                sketch.draw_line(xt, bottom_y - TICK_SIZE, xt, top_y-15)
                sketch.set_stroke('#000000')
                sketch.set_stroke_weight(1)
                sketch.draw_text(xt, bottom_y + TICK_SIZE + 5, f"{int((j+1)*100/n_x)}%")
                sketch.set_stroke_weight(2)
            sketch.pop_style()
        sketch.pop_style()

    def handle_click(self, x, y):
        # industry selector
        if x <= INDUSTRY_PAD:
            top_y = TOP_PAD + 60
            bottom_y = HEIGHT - BOTTOM_PAD - 6
            step = (bottom_y - top_y) / (len(INDUSTRIES) - 1)
            for i, industry in enumerate(INDUSTRIES):
                label_y = top_y + i * step
                if abs(y - label_y) < step/2:
                    self._selected_index = i
                    self._selected_industry = INDUSTRIES[i]
                    self._selected_bottom = [0, 0, 0]
                    return True
        
        # bottom tech clicks
        x0 = INDUSTRY_PAD + Y_AXIS_PAD
        full_w = (WIDTH - RIGHT_PAD/2) - x0
        slot_w = full_w / 3 - 30
        header_y = 535
        
        for i in range(3):
            xs = x0 + i * (slot_w + 45)
            for j in range(3):
                yj = header_y + j * (SMALL_SIZE + 4) - 30
                if xs < x < xs + slot_w and (yj - SMALL_SIZE) < y < (yj + SMALL_SIZE):
                    self._selected_bottom[i] = j
                    return True
        return False

    
    def handle_hover(self, x, y):
        if not hasattr(self, "_filtered"):
            return
        
        # industry hover
        self._hover_index = None
        top_y = TOP_PAD + 60
        bottom_y = HEIGHT - BOTTOM_PAD - 15
        step = (bottom_y - top_y) / (len(INDUSTRIES) - 1)
        if x <= INDUSTRY_PAD:
            for i in range(len(INDUSTRIES)):
                label_y = top_y + i * step
                if abs(y - label_y) < step / 2:
                    self._hover_index = i
                    break
        
        # bottom-tech hover
        self._hover_bottom = [None, None, None]
        x0 = INDUSTRY_PAD + Y_AXIS_PAD
        slot_w = ((WIDTH - RIGHT_PAD/2) - x0) / 3 - 30
        header_y = 545
        for i in range(3):
            xs = x0 + i * (slot_w + 45)
            for j in range(3):
                yj = header_y + j * (SMALL_SIZE + 4) - 35
                if xs < x < xs + slot_w and (yj - SMALL_SIZE/2) < y < (yj + SMALL_SIZE/2):
                    self._hover_bottom[i] = j
                    break
        
        # legend hover
        self._hover_edu = None
        legend_x = WIDTH - RIGHT_PAD/2 - LEGEND_WIDTH
        legend_y = TOP_PAD + 40
        n_lbl = len(EDUCATION)
        pad_v = 25
        usable = HEIGHT * (3/8) - 2 * pad_v
        spacing = usable / (n_lbl - 1)
        for k, ed in enumerate(EDUCATION):
            y_lbl = legend_y + pad_v + k * spacing
            if legend_x < x < legend_x + LEGEND_WIDTH and (y_lbl - SMALL_SIZE/2) < y < (y_lbl + SMALL_SIZE/2):
                self._hover_edu = ed
                return
        
        # big-chart line hover
        x0_line = INDUSTRY_PAD + Y_AXIS_PAD
        right_x = x0_line + LEN_X_AXIS
        bottom_y_line = TOP_PAD + 40 + LEN_Y_AXIS
        for ed, (m, b) in self._big_fit.items():
            x1, y1 = x0_line, bottom_y_line - b * self._yscale
            x2, y2 = right_x, bottom_y_line - (b + m * self._big_xmax) * self._yscale
            dx, dy = x2 - x1, y2 - y1
            denom = dx*dx + dy*dy
            if denom > 0:
                t = ((x - x1) * dx + (y - y1) * dy) / denom
                t = max(0, min(1, t))
                px, py = x1 + t*dx, y1 + t*dy
                if (x - px)**2 + (y - py)**2 <= 25:
                    self._hover_edu = ed
                    return
        
        # bottom-bar hover
        height = LEN_Y_AXIS * 2/3
        top_y_bar = header_y + SMALL_SIZE + 10
        bar_h = height / len(EDUCATION) * 0.6
        for i in range(3):
            xs = x0 + i * (slot_w + 45)
            sel_idx = self._selected_bottom[i]
            sel_tech = self._bottom_top3[i][sel_idx]
            pct = self._bottom_data[i][sel_tech]
            for k, ed in enumerate(EDUCATION):
                y_bar = top_y_bar + k * ((height / (len(EDUCATION) - 1)) - 3) - bar_h/2
                w = pct.get(ed, 0) * (slot_w / 100)
                if xs < x < xs + w and y_bar < y < y_bar + bar_h:
                    self._hover_edu = ed
                    return

    def _compute_big_stats(self):
        data = {}
        fits = {}
        for ed in EDUCATION:
            by_year = {}
            for r in self._filtered:
                if r.get('EdLevel') != ed:
                    continue
                try:
                    yr = int(float(r.get('YearsCode', '')))
                    pay = float(r.get('Salary', ''))
                except (ValueError, TypeError):
                    continue
                by_year.setdefault(yr, []).append(pay)
            
            medians = {}
            for yr, vals in by_year.items():
                vals.sort()
                medians[yr] = vals[len(vals)//2]
            if not medians:
                continue
            
            data[ed] = medians
            # prepare for weighted fit
            years  = sorted(medians.keys())
            values = [medians[y] for y in years]
            counts = [len(by_year[y]) for y in years]
            weights = counts
            W = sum(weights)
            # weighted means
            x_bar = sum(w*x for w,x in zip(weights, years))  / W
            y_bar = sum(w*y for w,y in zip(weights, values)) / W
            # compute slope & intercept
            num = sum(w*(x - x_bar)*(y - y_bar)
                      for w,x,y in zip(weights, years, values))
            den = sum(w*(x - x_bar)**2
                      for w,x in zip(weights, years))
            m = num/den if den else 0
            b = y_bar - m*x_bar
            fits[ed] = (m, b)
        
        self._big_data = data
        self._big_fit  = fits
        all_years = [yr for med in data.values() for yr in med]
        self._big_xmax = self._static_xmax
        self._big_ymax = self._static_ymax
        self._xscale = LEN_X_AXIS / self._big_xmax
        self._yscale = LEN_Y_AXIS / self._big_ymax
    
    def _compute_bottom_stats(self):
        self._bottom_top3 = []
        self._bottom_data = []
        
        for cols in (LANG_COLS, EDITOR_COLS, AI_COLS):
            # count usage
            usage_counts = {}
            for c in cols:
                cnt = sum(
                    1
                    for r in self._filtered
                    if (
                        float(r.get(c, 0) or 0) == 1.0
                        if r.get(c) not in (None, '')
                        else False
                    )
                )
                usage_counts[c] = cnt
            
            # pick top 3
            sorted_techs = sorted(
                usage_counts.items(),
                key=lambda kv: kv[1],
                reverse=True
            )
            top3 = [tech for tech, _ in sorted_techs[:3]]
            self._bottom_top3.append(top3)
            
            # for each of those 3, compute % by education
            tech_pct_map = {}
            for tech in top3:
                pct = {}
                for ed in EDUCATION:
                    group = [r for r in self._filtered if r.get('EdLevel') == ed]
                    if not group:
                        pct[ed] = 0
                        continue
                    cnt = sum(
                        1
                        for r in group
                        if float(r.get(tech, 0) or 0) == 1.0
                    )
                    pct[ed] = cnt / len(group) * 100
                tech_pct_map[tech] = pct
            
            self._bottom_data.append(tech_pct_map)

def main():
    sketch = sketchingpy.Sketch2DWeb(WIDTH, HEIGHT)
    sketch.set_angle_mode('degrees')
    records = sketch.get_data_layer().get_csv('./stack_overflow_survey2.csv')
    dashboard = SurveyDashboardPresenter(records)
    mouse = sketch.get_mouse()
    
    def on_press(button):
        if button.get_name() == sketchingpy.const.MOUSE_LEFT_BUTTON:
            x = mouse.get_pointer_x()
            y = mouse.get_pointer_y()
            dashboard.handle_click(x, y)
    
    def on_frame(s):
        x = mouse.get_pointer_x()
        y = mouse.get_pointer_y()
        dashboard.handle_hover(x, y)
        dashboard.draw(s)
    
    mouse.on_button_press(on_press)
    sketch.on_step(on_frame)
    sketch.show()

main()