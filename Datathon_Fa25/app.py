import streamlit as st
import pandas as pd
from main import (
    build_pipeline,
    display_output,
    MSI_CATEGORIES
)

st.set_page_config(page_title="College Match & ROI Tool", layout="wide")

st.title("🎓 College Match & ROI Explorer")

st.write(
    "Answer the questions below to generate a customized list of colleges "
    "ranked by similarity to your preferences, along with estimated return on investment."
)

# -------------------- FORM UI --------------------

with st.form("user_inputs"):

    st.subheader("Basic Preferences")

    state_pref = st.text_input(
        "Home State (2 letters, optional)",
        value="CA",
        help="Used only if you select 'In-State preference'."
    ).upper()

    residency_pref = st.selectbox(
        "Residency preference:",
        ["in_state", "oos", "any"]
    )

    family_earnings = st.number_input(
        "Estimated Family Income ($)",
        min_value=0,
        max_value=500000,
        step=500,
        value=60000
    )

    desired_degree = st.selectbox(
        "Highest Degree You Want the College to Offer:",
        ["non-degree", "associate", "bachelor", "master", "doctoral"]
    )

    st.subheader("Soft Preferences (Similarity Model)")

    sector = st.selectbox("Preferred Sector:", ["Public", "Private"])

    locality = st.selectbox("Preferred Campus Setting:", ["City", "Suburb", "Town", "Rural"])

    preferred_msi = st.selectbox(
        "Preferred Minority Serving Institution Type (optional):",
        ["none"] + MSI_CATEGORIES
    )

    st.write("Rate how strongly each preference matters (1 = small effect, 5 = very important):")

    col1, col2, col3 = st.columns(3)
    with col1:
        w_sector = st.slider("Sector Weight", 1, 5, 3)
        w_locality = st.slider("Locality Weight", 1, 5, 4)
        w_msi = st.slider("MSI Weight", 1, 5, 3)
    with col2:
        w_enrollment = st.slider("Enrollment Size Weight", 1, 5, 4)
        w_admit_rate = st.slider("Selectivity Weight (Admit Rate)", 1, 5, 4)
        w_ratio = st.slider("Student-Faculty Ratio Weight", 1, 5, 3)

    # 🧮 Submit
    submitted = st.form_submit_button("Find My Colleges ✨")

# -------------------- PROCESSING --------------------

if submitted:
    with st.spinner("Generating personalized matches..."):

        user_prefs = {
            "sector": sector,
            "locality": locality,
            "preferred_msi": preferred_msi if preferred_msi != "none" else None,
            "total_enrollment": 30000,  # could later expose as UI
            "admit_rate": 0.20,
            "student_faculty_ratio": 5
        }

        user_weights = {
            "sector": w_sector,
            "locality": w_locality,
            "msi": w_msi,
            "total_enrollment": w_enrollment,
            "admit_rate": w_admit_rate,
            "student_faculty_ratio": w_ratio
        }

        ranked_df = build_pipeline(
            state_pref,
            residency_pref,
            family_earnings,
            desired_degree,
            user_prefs,
            user_weights
        )

        results = display_output(ranked_df)

    st.success("Done! Scroll below to view your ranked colleges.")

    st.subheader("📊 Recommended Colleges")

    st.dataframe(results, use_container_width=True)

    # ---- Download Option ----
    csv = results.to_csv(index=False)
    st.download_button(
        label="Download Results as CSV",
        data=csv,
        file_name="college_matches.csv",
        mime="text/csv"
    )


# -------------------- FOOTER --------------------
st.markdown("---")
st.caption("Built with ❤️ using Streamlit and your college analytics engine.")
