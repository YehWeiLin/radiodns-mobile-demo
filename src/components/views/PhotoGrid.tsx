import * as React from "react";
import {Dimensions, FlatList, ListRenderItemInfo, View} from "react-native";

interface Props<T> {
    data: T[];
    itemsPerRow: number;
    itemMargin?: number;
    renderItem: (item: T, itemWidth: number) => JSX.Element;
}

/**
 * View containing a configurable image grid.
 */
export class PhotoGrid<T> extends React.Component<Props<T>> {

    public buildRows(items: T[], itemsPerRow = 3): T[][] {
        return items.reduce((rows, item, idx) => {
            // If a full row is filled create a new row array
            if (idx % itemsPerRow === 0 && idx > 0) {
                rows.push([]);
            }
            if (rows[rows.length - 1]) {
                rows[rows.length - 1].push(item);
            }
            return rows;
        }, new Array<T[]>());
    }

    public render() {
        const rows = this.buildRows(this.props.data, this.props.itemsPerRow);

        return (
            <FlatList
                {...this.props}
                data={rows}
                renderItem={this.renderRow}
                style={{flex: 1}}
            />
        );
    }

    private renderRow = (listRenderItemInfo: ListRenderItemInfo<T[]>) => {
        const items = listRenderItemInfo.item;
        // Calculate the width of a single item based on the device width
        // and the desired margins between individual items
        const deviceWidth = Dimensions.get("window").width;
        const itemsPerRow = this.props.itemsPerRow;
        const margin = this.props.itemMargin || 1;

        const totalMargin = margin * (itemsPerRow - 1);
        const itemWidth = Math.floor((deviceWidth - totalMargin) / itemsPerRow);
        const adjustedMargin = (deviceWidth - (itemsPerRow * itemWidth)) / (itemsPerRow - 1);

        return (
            <View
                style={{
                    flex: 1,
                    flexDirection: "row",
                    justifyContent: "space-between",
                    marginBottom: adjustedMargin,
                }}
            >
                {items.map((item) => this.props.renderItem(item, itemWidth))}
            </View>
        );
    }
}
