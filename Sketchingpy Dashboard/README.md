# Stack Overflow 2024 Developer Survey Dashboard

This project uses the **2024 Stack Overflow Developer Survey** data to build an interactive dashboard in [Sketchingpy](https://editor.sketchingpy.org/). The dashboard explores how different education levels affect **salary** and **technology use** across industries.

---

## Project Overview
- **Goal**: Visualize relationships between education level, salary, coding experience, and technology usage across industries.  
- **Preprocessing**: Data was cleaned in a Jupyter notebook, including:
  - Multi-hot encoding of technologies used.  
  - Removing outliers by filtering the bottom and top 1% of salaries per industry/education level.  
- **Dashboard Features**:
  - Left-hand panel of clickable industries.  
  - **Top Graph**: Weighted line of best fit for median salary vs. years of coding experience, split by education level.  
  - **Bottom Graphs**: Most popular technologies per industry (coding languages, editors, AI tools). Users can click text labels to swap between the top 3 in each category.  
  - Hovering over lines, bars, or legend highlights a degree level for clarity.  

---

## Repository Contents
- `Final Project Data Preprocessing (1).ipynb` — Jupyter notebook for data cleaning and preprocessing.  
- `stack_overflow_survey2.csv` — Preprocessed dataset used in the dashboard.  
- `Sketchingpy Dashboard Project.skprj` — Sketchingpy project file. Navigate to [Sketchingpy](https://editor.sketchingpy.org/), click "Open Project," upload this file, and "Run" the code to use the interactive dashboard.  
- `Sketchingpy Dashboard Code.py` — Python script version of the Sketchingpy code (for easier viewing in GitHub).  
- `Sketchingpy Project Demo.gif` — Short demo of the interactive dashboard.  

---

## Demo
<p align="center">
  <img src="Sketchingpy Project Demo.gif" width="700"/>
</p>

---

## Key Insights
- Higher education generally correlates with higher salaries, but the difference is smaller than expected and varies by industry.  
- Professional degrees often only slightly outperform secondary education in salary.  
- Python is not the top language in every industry—its dominance varies.  
- ChatGPT is widely used across industries and education levels.  

---

## License
Released under the BSD-3 Clause License.  
