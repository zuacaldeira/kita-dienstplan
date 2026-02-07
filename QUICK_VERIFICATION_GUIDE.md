# Quick Verification Guide - Schedule Table View

## 🚀 Quick Start (5 minutes)

### 1. Start the Application

```bash
# Terminal 1 - Backend
cd backend
mvn spring-boot:run

# Terminal 2 - Frontend
cd frontend
ng serve
```

### 2. Open Browser

```
URL: http://localhost:4200
Login: uwe / password123
```

### 3. Navigate to Schedule Tab

Click the "Dienstplan" tab (second tab with calendar icon)

---

## ✅ Visual Verification Checklist

### Layout Structure
- [ ] Table layout visible (not cards)
- [ ] Column headers: Mitarbeiter | Montag | Dienstag | Mittwoch | Donnerstag | Freitag
- [ ] Staff names in first column
- [ ] Staff roles displayed under names (smaller text)
- [ ] Rows sorted alphabetically by staff name

### Cell Content
- [ ] Filled cells show either:
  - Times in format "HH:MM - HH:MM" (for NORMAL status)
  - Status text only (for FREI/KRANK/URLAUB/FORTBILDUNG)
- [ ] Empty cells are blank (with faint + icon on hover)

### Colors (Status-based backgrounds)
- [ ] Green cells (#e8f5e9) → NORMAL entries
- [ ] Blue cells (#e3f2fd) → FREI entries
- [ ] Red cells (#ffebee) → KRANK entries
- [ ] Orange cells (#fff3e0) → URLAUB entries
- [ ] Purple cells (#f3e5f5) → FORTBILDUNG entries

### Interactive Elements
- [ ] Week navigation arrows work (left/right of "KW XX / YYYY")
- [ ] Hover over filled cell → Shows [×] delete button
- [ ] Hover over empty cell → Shows faint [+] icon
- [ ] Table header is sticky (stays visible when scrolling)

---

## 🧪 Functional Testing (10 minutes)

### Test 1: Week Navigation ✅

1. Note current week number
2. Click left arrow (◀)
3. **Expected:** Week number decreases, table updates with different data
4. Click right arrow (▶) twice
5. **Expected:** Week number increases, table updates

**Status:** [ ] PASS / [ ] FAIL

---

### Test 2: Edit Existing Entry ✅

1. Click any cell that contains times (e.g., "08:00 - 16:00")
2. **Expected:** Dialog opens with title "Dienstplan bearbeiten"
3. **Expected:** Form fields are pre-filled with:
   - Staff name (selected)
   - Day of week (selected)
   - Start time
   - End time
   - Status
4. Change start time (e.g., from 08:00 to 08:30)
5. Click "Speichern"
6. **Expected:** Dialog closes, table refreshes, cell shows new time

**Status:** [ ] PASS / [ ] FAIL

---

### Test 3: Delete Entry ✅

1. Hover over a filled cell
2. **Expected:** Small [×] button appears in top-right of cell
3. Click the [×] button
4. **Expected:** Confirmation dialog appears
5. **Expected:** Message shows staff name and day
6. Click "Löschen"
7. **Expected:** Cell becomes empty, [×] button gone

**Status:** [ ] PASS / [ ] FAIL

---

### Test 4: Create New Entry ✅

1. Find an empty cell (no content)
2. Click the empty cell
3. **Expected:** Dialog opens with title "Dienstplan erstellen"
4. **Expected:** Staff and day are pre-selected based on clicked cell
5. Fill in:
   - Start time: 09:00
   - End time: 17:00
   - Status: NORMAL
6. Click "Speichern"
7. **Expected:** Cell now shows "09:00 - 17:00" with green background

**Status:** [ ] PASS / [ ] FAIL

---

### Test 5: Create Entry with Status ✅

1. Click an empty cell
2. In the dialog, select Status: FREI (instead of NORMAL)
3. Click "Speichern"
4. **Expected:** Cell shows "FREI" text with blue background
5. **Expected:** No times shown (status only)

**Status:** [ ] PASS / [ ] FAIL

---

### Test 6: Responsive Design ✅

1. Resize browser window to narrow width (< 768px)
2. **Expected:** Table becomes horizontally scrollable
3. **Expected:** First column (staff names) remains somewhat visible
4. Scroll table horizontally
5. **Expected:** Can see all weekday columns by scrolling

**Status:** [ ] PASS / [ ] FAIL

---

### Test 7: Multiple Staff Display ✅

1. Ensure current week has entries for at least 3 different staff members
2. **Expected:** Each staff member has exactly one row
3. **Expected:** Rows are alphabetically sorted
4. **Expected:** Same staff doesn't appear in multiple rows

**Status:** [ ] PASS / [ ] FAIL

---

## 🔍 Data Integrity Check

### Verify No Data Loss

**Before table implementation:**
```bash
# Count total schedule entries in database
mysql -u kita_admin -p kita_casa_azul -e "SELECT COUNT(*) FROM schedule_entries;"
```

**After testing:**
```bash
# Count should be same or higher (if you added entries)
mysql -u kita_admin -p kita_casa_azul -e "SELECT COUNT(*) FROM schedule_entries;"
```

**Status:** [ ] Same or higher / [ ] LOST DATA (investigate!)

---

## 🎨 Visual Regression Check

### Compare with PDF Schedule

1. Open a PDF schedule from `data/extracted-schedules/`
   - Example: `PDienstplan 250825-250829.pdf` (KW 35/2025)

2. Navigate to same week in web app
   - Select KW 35, Year 2025

3. Compare visually:

| Aspect | PDF | Web Table | Match? |
|--------|-----|-----------|--------|
| Staff order | Alphabetical | ? | [ ] |
| Column headers | Mon-Fri | ? | [ ] |
| Cell content format | Times or status | ? | [ ] |
| Color coding | Status colors | ? | [ ] |
| Weekly overview | One page | ? | [ ] |

**Overall Visual Match:** [ ] Good / [ ] Needs adjustment

---

## ⚡ Performance Check

### Load Time Test

1. Open browser DevTools (F12)
2. Go to Network tab
3. Navigate to Schedule tab
4. Check API response time:

```
GET /api/schedules/week?year=2025&week=35

Expected: < 500ms
Actual: _______ms

Status: [ ] PASS (<500ms) / [ ] SLOW (>500ms)
```

### Rendering Performance

1. Open browser DevTools (F12)
2. Go to Performance tab
3. Start recording
4. Click Schedule tab
5. Stop recording
6. Check rendering time

```
Expected: < 200ms for table render
Actual: _______ms

Status: [ ] PASS (<200ms) / [ ] SLOW (>200ms)
```

---

## 🐛 Known Issues Check

### Issue 1: Day Index Mismatch

**Symptom:** Delete confirmation shows wrong day name

**Test:**
1. Delete a Monday entry
2. Check confirmation dialog
3. **Expected:** Message says "Montag"
4. **Actual:** Says "______"

**Status:** [ ] FIXED / [ ] BUG PRESENT

---

### Issue 2: Empty Week Display

**Test:**
1. Navigate to a week with NO schedule entries
2. **Expected:** Empty state message displayed
3. **Expected:** "Keine Dienstpläne für diese Woche verfügbar"

**Status:** [ ] PASS / [ ] FAIL

---

### Issue 3: Weekend Entries

**Test:**
1. Create an entry for Saturday or Sunday (if possible)
2. **Expected:** Entry is saved but NOT shown in table
3. **Reason:** Table only displays Mon-Fri

**Status:** [ ] Correctly hidden / [ ] Incorrectly shown

---

## 📱 Cross-Browser Check

Test in multiple browsers:

### Chrome/Edge
- [ ] Layout correct
- [ ] Colors correct
- [ ] Interactions work
- [ ] Performance good

### Firefox
- [ ] Layout correct
- [ ] Colors correct
- [ ] Interactions work
- [ ] Performance good

### Safari (if available)
- [ ] Layout correct
- [ ] Colors correct
- [ ] Interactions work
- [ ] Performance good

---

## 🖨️ Print Test

1. Navigate to Schedule tab with data
2. Open print preview (Ctrl+P / Cmd+P)
3. **Expected:**
   - [ ] Table layout preserved
   - [ ] Status colors visible (not grayscale)
   - [ ] Header visible
   - [ ] No scrollbars in print
   - [ ] Fits on one page (or clearly paginated)

---

## ✨ User Experience Check

### Ease of Use (Subjective)

Rate each task (1=Hard, 5=Easy):

| Task | Rating | Notes |
|------|--------|-------|
| Find a specific staff's schedule | __/5 | |
| See weekly coverage at a glance | __/5 | |
| Identify gaps in coverage | __/5 | |
| Add new schedule entry | __/5 | |
| Edit existing entry | __/5 | |
| Compare multiple staff | __/5 | |
| Navigate between weeks | __/5 | |

**Average Rating:** ____/5

**Overall UX:** [ ] Excellent (4.5+) / [ ] Good (3.5-4.4) / [ ] Needs Work (<3.5)

---

## 📊 Comparison Test

### Side-by-Side Comparison

If you still have the old card view code:

1. Test same tasks in both views
2. Time each task
3. Record which is faster

| Task | Card View | Table View | Winner |
|------|-----------|------------|--------|
| Find Alice's Monday | __s | __s | |
| See all Monday entries | __s | __s | |
| Spot coverage gap | __s | __s | |
| Add entry for specific day/staff | __s | __s | |

---

## 🎯 Success Criteria

### Minimum Requirements (Must Pass)

- [ ] Table layout displays correctly
- [ ] All CRUD operations work (Create, Read, Update, Delete)
- [ ] Week navigation works
- [ ] Colors match specification
- [ ] No console errors
- [ ] No data loss
- [ ] Responsive design works

### Excellence Criteria (Nice to Have)

- [ ] Performance < 200ms render time
- [ ] Visual match with PDF > 90%
- [ ] Average UX rating > 4.0
- [ ] Works in all major browsers
- [ ] Print layout looks professional
- [ ] Zero accessibility warnings

---

## 📝 Final Checklist

Before considering implementation complete:

- [ ] All minimum requirements pass
- [ ] At least 5/7 functional tests pass
- [ ] No critical bugs found
- [ ] Performance acceptable
- [ ] Documentation updated
- [ ] Code committed to git (if applicable)

---

## 🔧 Troubleshooting

### Table Not Displaying

**Check:**
```bash
# Browser console (F12)
# Look for errors like:
# - "Cannot find module..."
# - "Property 'tableData' does not exist..."
```

**Fix:**
```bash
cd frontend
npm install
ng serve
```

---

### Wrong Colors Showing

**Check:** Browser DevTools > Elements > Inspect cell
**Expected classes:** `cell-normal`, `cell-frei`, `cell-krank`, etc.

**If missing:** Check status value in API response

---

### Delete Button Not Appearing

**Check:** CSS is loaded
**Test:** Hover and wait 200ms (transition delay)
**Fix:** Clear browser cache (Ctrl+Shift+R)

---

### Week Navigation Broken

**Check:** Console for API errors
**Expected API call:** `GET /api/schedules/week?year=2025&week=35`

**If failing:** Backend might not be running

---

## 📞 Support

If tests fail:

1. Check browser console for errors
2. Check network tab for failed API calls
3. Verify backend is running (http://localhost:8080)
4. Check `SCHEDULE_TABLE_IMPLEMENTATION.md` for details
5. Review code changes in git diff

---

## ✅ Test Results Summary

Fill this out after testing:

```
Date: _____________
Tester: _____________

Visual Checks: ___/10 passed
Functional Tests: ___/7 passed
Browser Tests: ___/3 browsers
Performance: [ ] PASS / [ ] FAIL
Data Integrity: [ ] PASS / [ ] FAIL

Overall Result: [ ] APPROVED / [ ] NEEDS FIXES

Notes:
_________________________________
_________________________________
_________________________________
```

---

## 🎉 Success!

If all tests pass:

**Next Steps:**
1. Show to stakeholders for feedback
2. Consider adding weekly hours summary
3. Consider export to PDF feature
4. Update user documentation
5. Train users on new interface

**Congratulations!** The schedule table view is working correctly! 🎊
