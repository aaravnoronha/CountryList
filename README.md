Data Model: 
Country.kt

Defines the data structure for country information, Contains properties: name, code, capital, region, Implements Parcelable for data transfer between components

Layout Files: 
activity_main.xml

Main layout file containing RecyclerView, Implements ConstraintLayout as the root layout, Displays the list of countries

country_item.xml

Layout template for individual country items in the list, Uses ConstraintLayout for positioning elements, Contains TextViews for country name, region, code, and capital, Includes a divider line between items

Resource Files: 
themes.xml

Defines the app's theme, Removes action bar, Sets white background and transparent status bar

colors.xml

Contains color definitions used throughout the app, Defines primary, secondary, and accent colors

Adapter:
CountryAdapter.kt

Manages the RecyclerView data, Binds country data to the item layout, Handles view recycling and efficiency, Implements ViewHolder pattern

Activity:
MainActivity.kt

Entry point of the application, Initializes RecyclerView and adapter, Manages data loading and display, Implements error handling and lifecycle management
